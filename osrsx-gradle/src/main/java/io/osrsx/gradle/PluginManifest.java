package io.osrsx.gradle;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The manifest contract between a plugin jar built by the {@code io.osrsx.plugin} Gradle plugin and the
 * host's {@code PluginManager} (Phase 2/4 consumer).
 *
 * <p>The four fields are written as {@code MANIFEST.MF} main attributes. The attribute names below are
 * the stable contract — the host reads exactly these keys off {@code JarFile.getManifest()}:
 *
 * <pre>
 *   Osrsx-Plugin-Id:       stable identifier, e.g. "my-woodcutter"
 *   Osrsx-Plugin-Name:     human display name
 *   Osrsx-Plugin-Version:  the plugin's own version
 *   Osrsx-Api-Version:     the io.osrsx:osrsx-api version it was compiled against
 * </pre>
 *
 * <p>Kept as a pure, dependency-free function so it is unit-testable without a Gradle build.
 */
public final class PluginManifest {

    public static final String ATTR_ID = "Osrsx-Plugin-Id";
    public static final String ATTR_NAME = "Osrsx-Plugin-Name";
    public static final String ATTR_VERSION = "Osrsx-Plugin-Version";
    public static final String ATTR_API_VERSION = "Osrsx-Api-Version";

    private PluginManifest() {
    }

    /**
     * Build the ordered map of manifest main attributes for a plugin jar. Blank/null values are
     * rejected: an unstamped plugin jar would be indistinguishable from an ordinary library, so the
     * caller must supply every field.
     */
    public static Map<String, String> attributes(String id, String name, String version, String apiVersion) {
        Map<String, String> attrs = new LinkedHashMap<>();
        attrs.put(ATTR_ID, require(id, "id"));
        attrs.put(ATTR_NAME, require(name, "name"));
        attrs.put(ATTR_VERSION, require(version, "version"));
        attrs.put(ATTR_API_VERSION, require(apiVersion, "apiVersion"));
        return attrs;
    }

    private static String require(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("osrsxPlugin." + field + " must be set");
        }
        return value.trim();
    }
}
