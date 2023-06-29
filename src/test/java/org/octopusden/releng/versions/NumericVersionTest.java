package org.octopusden.releng.versions;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class NumericVersionTest {
    private static final String STR_V_1_2_3_4 = "1.2.3.4";
    private static final String STR_V_1_2_3 = "1.2.3";
    private static final IVersionInfo VERSION_1_2_3_4 = version(STR_V_1_2_3_4);

    private static final VersionNames VERSION_NAMES = new VersionNames("serviceCBranch", "serviceC", "minorC");

    private static final NumericVersion.Builder BUILDER = new NumericVersion.Builder(VERSION_NAMES);
    private static final IVersionInfo VERSION_1_2_3 = BUILDER.setRawVersion(STR_V_1_2_3).build();

    private static IVersionInfo version(String version) {
        return BUILDER.setRawVersion(version).build();
    }

    @Test
    void testCompareVersions() {
        assertThat(version("1.2.3.5"), greaterThan(VERSION_1_2_3_4));
        assertThat(VERSION_1_2_3_4, lessThan(version("1.2.3.5")));

        assertThat(VERSION_1_2_3_4, equalTo(version("1.2.3.4.0")));
        assertThat(VERSION_1_2_3_4, lessThan(version("1.2.3.4.1")));
        assertThat(VERSION_1_2_3_4, lessThan(version("1.2.3.4-1")));

        assertThat(VERSION_1_2_3_4, lessThan(version("1.2.3.4-0001")));
        assertThat(version("1.2.3-0001"), lessThan(version("1.2.3-0002")));
        assertThat(version("1.2.3-1"), lessThan(version("1.2.3-0002")));
        assertThat(version("1.2.3-0001"), lessThan(version("1.2.3-2")));

        assertThat(VERSION_1_2_3_4, greaterThan(version(STR_V_1_2_3)));

        assertThat(version("1"), lessThan(version("2")));
    }

    @Test
    void testEqual() {
        assertThat(VERSION_1_2_3_4, not(equalTo(version("1.2.3.5"))));
        assertThat(VERSION_1_2_3_4, equalTo(VERSION_1_2_3_4));
        assertThat(version("1.2.3.4-1"), equalTo(version("1.2.3.4-0001")));
    }

    @Test
    void testRCVersion() {
        IVersionInfo rcVersion = version("1.2.3_RC");
        assertThat(rcVersion, equalTo(version("1.2.3")));
    }

    @Test
    void testStringVersion() {
        assertThat(version("zenit"), equalTo(version("0")));
    }

    @Test
    void testNull() {
        assertThrows(NullPointerException.class, ()-> version(null));
    }

    @Test
    void testNegative() {
        assertThat(version(""), equalTo(version("0")));
    }

    @Test
    void testRawVersion() {
        assertEquals(STR_V_1_2_3_4, VERSION_1_2_3_4.toString());
    }

    @Test
    void testVersions() {
        assertThat(version("03.37.30"), greaterThan(version("03.36.20.19")));
        assertThat(version("03.37.30.13"), lessThan(version("03.37.30.19")));
    }

    @Test
    void testMinorVersion() {
        assertMinorVersionCalculation("03.45.00", "03.44.99.10");
        assertMinorVersionCalculation("03.45.01", "03.45.00.01");
        assertMinorVersionCalculation("03.45.02", "03.45.01.01");
        assertMinorVersionCalculation("03.45.10", "03.45.09.10");
        assertMinorVersionCalculation("03.45.11", "03.45.10.00");
        assertMinorVersionCalculation("03.45.12", "03.45.11.11");
        assertMinorVersionCalculation("03.45.20", "03.45.19.10");
        assertMinorVersionCalculation("03.45.21", "03.45.20.01");
        assertMinorVersionCalculation("03.45.22", "03.45.21.01");
        assertMinorVersionCalculation("03.45.30", "03.45.29.01");
        assertMinorVersionCalculation("03.45.31", "03.45.30.01");
        assertMinorVersionCalculation("03.45.32", "03.45.31.01");
        assertMinorVersionCalculation("03.45.33", "03.45.32.12");
    }

    private void assertMinorVersionCalculation(String minorVersion, String releaseVersion) {
        String actual = BUILDER.setRawVersion(releaseVersion).build().formatVersion("$major02.$minorC.$serviceC");
        assertEquals(minorVersion, actual, String.format("|%s|%s|%s|", releaseVersion, minorVersion.substring(0, 7), minorVersion));
    }

    @Test
    void testParseVersion() {
        assertThat(2, equalTo(version("2-1").getItem(0)));
        assertThat(1, equalTo(version("2-1").getItem(1)));
        assertThat(0, equalTo(version("2-1").getItem(2)));
        assertFalse(version("2-1").isSnapshot());
        assertThat(1, equalTo(version("2-0001").getItem(1)));
        assertThat(2, equalTo(version("2-0001").getItemsCount()));
        assertThat(3, equalTo(version("1.2.003").getItemsCount()));
        assertTrue(version("1.0-SNAPSHOT").isSnapshot());

        assertEquals(1, version("Model.1.2").getMajor());
        assertEquals(2, version("Model.1.2").getMinor());

        assertEquals(3, version("03.37.30").getItemsCount());
        assertEquals(3, version("03.37.30").getMajor());
    }

    @Test
    void testFormatVersion() {
        assertThat(VERSION_1_2_3.formatVersion("$major.$minor.$service"), equalTo(STR_V_1_2_3));
        assertThat(VERSION_1_2_3.formatVersion("MyComponent.$major.$minor.$service"), equalTo("MyComponent.1.2.3"));
        assertEquals("Mojo.1_2_3", BUILDER.setRawVersion("Mojo.$STR_V_1_2_3").build().formatVersion("Mojo.$major_$minor_$service"));
    }

    @Test
    void testSameVersionFormat() {
        assertEquals(STR_V_1_2_3, VERSION_1_2_3.formatVersion("$major.$minor.$service"));
        assertEquals("Mojo.1_2_3", BUILDER.setRawVersion(STR_V_1_2_3).build().formatVersion("Mojo.$major_$minor_$service"));
    }
}
