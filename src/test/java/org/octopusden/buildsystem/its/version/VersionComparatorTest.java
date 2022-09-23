package org.octopusden.buildsystem.its.version;

import org.octopusden.releng.versions.ReversedVersionComparator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionComparatorTest {
    private static final String v1 = "1.2.1-1";
    private static final String v2 = "1.2.1-5";
    private static final String v3 = "1.2.1-10";
    private static final String v4 = "1.2.1-15";
    private static final String v5 = "1.2";
    private static final String v6 = "1.2.1";
    private static final String v7 = "1.3";
    private static final String v8 = "1.1";

    @Test
    void testReversedComparator() {
        assertTrue(new ReversedVersionComparator().compare(v1, v2) > 0);
    }

    @Test
    void testCompare() {
        List<String> versions = Arrays.asList(v2, v4, v3, v1, v5, v6, v7, v8);
        versions.sort(new ReversedVersionComparator());
        assertEquals(v7, versions.get(0));
        assertEquals(v4, versions.get(1));
        assertEquals(v3, versions.get(2));
        assertEquals(v2, versions.get(3));
        assertEquals(v1, versions.get(4));
        assertEquals(v6, versions.get(5));
        assertEquals(v5, versions.get(6));
        assertEquals(v8, versions.get(7));
    }

    // RELENG-150
    @Test
    void testCompareDifferentFormats() {
        List<String> versions =Arrays.asList("Azericard.2", "2.0.143");
        versions.sort(new ReversedVersionComparator());
        assertEquals("2.0.143", versions.get(0));
        assertEquals("Azericard.2", versions.get(1));
    }
}
