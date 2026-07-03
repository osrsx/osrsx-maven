package io.osrsx.gradle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;

/**
 * {@code io.osrsx.plugin} — the convention plugin an external osrsx-plugin project applies.
 *
 * <p>Applying it:
 * <ul>
 *   <li>brings in the {@code java-library} base (so there is a {@code jar} to stamp),</li>
 *   <li>adds {@code mavenLocal()} + {@code mavenCentral()} so the SDK resolves during local dev,</li>
 *   <li>adds {@code compileOnly "io.osrsx:osrsx-api:<apiVersion>"} — plugins compile against the SDK;
 *       the host provides it at runtime,</li>
 *   <li>stamps the plugin jar's manifest with id/name/version/api-version (see {@link PluginManifest}),</li>
 *   <li>registers the {@code installPlugin} and {@code osrsxRun} dev-loop tasks.</li>
 * </ul>
 */
public class OsrsxPluginPlugin implements Plugin<Project> {

    /** Group/artifact of the SDK the plugin compiles against (version is resolved per-project). */
    private static final String SDK_GROUP_ARTIFACT = "io.osrsx:osrsx-api";
    /** Host plugin directory the client's PluginManager scans on startup. */
    private static final String PLUGINS_DIR = ".osrsx/plugins";
    private static final String TASK_GROUP = "osrsx";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("java-library");

        project.getRepositories().mavenLocal();
        project.getRepositories().mavenCentral();

        OsrsxPluginExtension ext = project.getExtensions()
                .create("osrsxPlugin", OsrsxPluginExtension.class);

        // Host-matched defaults so a minimal config only needs `id`.
        String builtAgainstSdk = builtAgainstSdkVersion();
        Provider<String> id = ext.getId();
        Provider<String> name = ext.getName().orElse(ext.getId());
        Provider<String> version = ext.getVersion().orElse(project.provider(() -> project.getVersion().toString()));
        Provider<String> apiVersion = ext.getApiVersion().orElse(builtAgainstSdk);

        addSdkDependency(project, apiVersion);
        stampJarManifest(project, id, name, version, apiVersion);
        registerInstallTask(project);
        registerRunTask(project);
    }

    /** {@code compileOnly "io.osrsx:osrsx-api:<apiVersion>"} — resolved lazily from the extension. */
    private void addSdkDependency(Project project, Provider<String> apiVersion) {
        Provider<Dependency> dep = apiVersion.map(v -> project.getDependencies().create(SDK_GROUP_ARTIFACT + ":" + v));
        project.getConfigurations().named("compileOnly").configure(c -> c.getDependencies().addLater(dep));
    }

    /**
     * Write id/name/version/api-version into the {@code jar} task's manifest. Done in {@code doFirst}
     * so the extension's lazy providers are resolved (and validated with a friendly message) right
     * before the archive is assembled.
     */
    private void stampJarManifest(Project project, Provider<String> id, Provider<String> name,
                                  Provider<String> version, Provider<String> apiVersion) {
        project.getTasks().named("jar", Jar.class).configure(jar -> jar.doFirst(t -> {
            Map<String, String> attrs = PluginManifest.attributes(
                    id.getOrNull(), name.getOrNull(), version.getOrNull(), apiVersion.getOrNull());
            jar.getManifest().attributes(attrs);
        }));
    }

    /** {@code installPlugin} — copy the built plugin jar into {@code ~/.osrsx/plugins}. */
    private void registerInstallTask(Project project) {
        TaskProvider<Jar> jar = project.getTasks().named("jar", Jar.class);
        File pluginsDir = new File(System.getProperty("user.home"), PLUGINS_DIR);
        project.getTasks().register("installPlugin", Copy.class, task -> {
            task.setGroup(TASK_GROUP);
            task.setDescription("Copy the built plugin jar into ~/.osrsx/plugins for the host to discover.");
            task.from(jar.flatMap(Jar::getArchiveFile));
            task.into(pluginsDir);
            task.doLast(t -> t.getLogger().lifecycle("[osrsx] installed plugin jar into {}", pluginsDir));
        });
    }

    /**
     * {@code osrsxRun} — dev-loop convenience. Launching the full host from an external project is
     * out of scope for Phase 1 (the host is the :osrsx-core checkout, not a published launchable), so
     * this installs the jar and points at the host's own run task. Phase 2's directory watcher makes
     * this a live hot-reload loop; see the module README.
     */
    private void registerRunTask(Project project) {
        project.getTasks().register("osrsxRun", task -> {
            task.setGroup(TASK_GROUP);
            task.setDescription("Install this plugin, then print how to launch the osrsx host with it loaded.");
            task.dependsOn("installPlugin");
            task.doLast(t -> {
                t.getLogger().lifecycle("[osrsx] plugin installed to ~/.osrsx/plugins.");
                t.getLogger().lifecycle("[osrsx] launch the host from an osrsx checkout with:");
                t.getLogger().lifecycle("[osrsx]     ./gradlew :osrsx-core:runClient");
                t.getLogger().lifecycle("[osrsx] (STUB: launching the host from an external project is a Phase 2 concern.)");
            });
        });
    }

    /** Read the SDK version this Gradle plugin was built against from the bundled resource. */
    private static String builtAgainstSdkVersion() {
        try (InputStream in = OsrsxPluginPlugin.class.getResourceAsStream("/osrsx-gradle.properties")) {
            if (in == null) {
                return "unspecified";
            }
            Properties props = new Properties();
            props.load(in);
            return props.getProperty("sdk.version", "unspecified");
        } catch (IOException e) {
            return "unspecified";
        }
    }
}
