package io.osrsx.gradle;

import org.gradle.api.provider.Property;

/**
 * The {@code osrsxPlugin { ... }} DSL an author configures in their plugin project's build script:
 *
 * <pre>
 *   osrsxPlugin {
 *       id = "my-woodcutter"          // required — stable plugin identifier
 *       name = "My Woodcutter"         // defaults to id
 *       // version    — defaults to project.version
 *       // apiVersion — defaults to the osrsx-api version this Gradle plugin was built against
 *   }
 * </pre>
 *
 * <p>{@code version} and {@code apiVersion} have host-matched defaults, so a minimal config sets only
 * {@code id}. The four resolved values become the plugin jar's manifest attributes (see
 * {@link PluginManifest}).
 */
public abstract class OsrsxPluginExtension {

    /** Stable plugin identifier written to {@code Osrsx-Plugin-Id}. Required. */
    public abstract Property<String> getId();

    /** Human display name written to {@code Osrsx-Plugin-Name}. Defaults to {@link #getId()}. */
    public abstract Property<String> getName();

    /** The plugin's own version written to {@code Osrsx-Plugin-Version}. Defaults to {@code project.version}. */
    public abstract Property<String> getVersion();

    /**
     * The {@code io.osrsx:osrsx-api} SDK version this plugin compiles against, written to
     * {@code Osrsx-Api-Version}. Defaults to the SDK version this Gradle plugin was built against, and
     * is also the coordinate used for the injected {@code compileOnly} dependency.
     */
    public abstract Property<String> getApiVersion();
}
