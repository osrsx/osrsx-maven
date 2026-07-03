package io.osrsx.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

class PluginManifestTest {

    @Test
    void writesAllFourAttributesUnderTheContractKeys() {
        Map<String, String> attrs = PluginManifest.attributes("my-woodcutter", "My Woodcutter", "1.2.0", "0.1.0");

        assertEquals("my-woodcutter", attrs.get("Osrsx-Plugin-Id"));
        assertEquals("My Woodcutter", attrs.get("Osrsx-Plugin-Name"));
        assertEquals("1.2.0", attrs.get("Osrsx-Plugin-Version"));
        assertEquals("0.1.0", attrs.get("Osrsx-Api-Version"));
        assertEquals(4, attrs.size());
    }

    @Test
    void trimsSurroundingWhitespace() {
        Map<String, String> attrs = PluginManifest.attributes("  id  ", " name ", " 1.0 ", " 0.1.0 ");

        assertEquals("id", attrs.get("Osrsx-Plugin-Id"));
        assertEquals("name", attrs.get("Osrsx-Plugin-Name"));
        assertEquals("1.0", attrs.get("Osrsx-Plugin-Version"));
        assertEquals("0.1.0", attrs.get("Osrsx-Api-Version"));
    }

    @Test
    void rejectsBlankRequiredFields() {
        assertThrows(IllegalArgumentException.class,
                () -> PluginManifest.attributes("", "name", "1.0", "0.1.0"));
        assertThrows(IllegalArgumentException.class,
                () -> PluginManifest.attributes("id", "name", null, "0.1.0"));
        assertThrows(IllegalArgumentException.class,
                () -> PluginManifest.attributes("id", "name", "1.0", "   "));
    }
}
