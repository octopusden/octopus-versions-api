package org.octopusden.releng.versions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentVersionFormatterTest {

    private static final VersionNames VERSION_NAMES = new VersionNames("serviceCBranch", "serviceC", "minorC");

    private static
    final ComponentVersionFormat componentVersionFormat = ComponentVersionFormat.create(
            "Model.$major.$minor.$service",
            "Model.$major.$minor.$service.$fix",
            "Model.$major.$minor.$service.$fix-$build",
            "Model.$major.$minor",
            "Model.$major.$minor.$service.$fix");

    private final ComponentVersionFormatter componentVersionFormatter = new ComponentVersionFormatter();
    private final IVersionInfo version = new NumericVersionFactory(VERSION_NAMES).create("1.2.3.4.5");


    @Test
    void testFormatMajor() {
        assertEquals("Model.1.2.3", componentVersionFormatter.formatMajor(version, componentVersionFormat));
    }


    @Test
    void testFormatRelease() {
        assertEquals("Model.1.2.3.4", componentVersionFormatter.formatRelease(version, componentVersionFormat));
    }

    @Test
    void testFormatBuildVersion() {
        assertEquals("Model.1.2.3.4-5", componentVersionFormatter.formatBuildVersion(version, componentVersionFormat));
    }

    @Test
    void testLineVersion() {
        assertEquals("Model.1.2", componentVersionFormatter.formatLineVersion(version, componentVersionFormat));
    }

    @Test
    void testHotFixVersion() {
        assertEquals("Model.1.2.3.4", componentVersionFormatter.formatHotFixVersion(version, componentVersionFormat));
    }
}
