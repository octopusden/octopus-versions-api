package org.octopusden.releng.versions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KotlinVersionFormatterTest {

    private static final String SIMPLE_FORMAT = "$major.$minor.$service";
    private static final String SIMPLE_FORMAT2 = "$major.$minor.$service-$fix";
    private static final String BUILD_VERSION_FORMAT = "$major.$minor.$service.$fix-$build";
    private static final String COMPONENT_FORMAT = "MyComponent.$major.$minor.$service";
    private static final String BRANCH_FORMAT = "C_$major02_$minorC_$serviceCBranch";
    private static final String BRANCH_FORMAT_WITH_BRACES = "C_${major02}_$minorC_$serviceCBranch";
    private static final String FORMAT = "$major02.$minorC.$serviceC";
    private static final VersionNames VERSION_NAMES = new VersionNames("serviceCBranch", "serviceC", "minorC");
    private static final NumericVersionFactory NUMERIC_VERSION_FACTORY = new NumericVersionFactory(VERSION_NAMES);

    private VersionFormatter formatter = new KotlinVersionFormatter(VERSION_NAMES);

    @Test
    void testPostProcessorList() {
        assertEquals("1-2-3", new KotlinVersionFormatter(VERSION_NAMES).getPREDEFINED_POSTPROCESSOR_LIST().get(3).component2().invoke("", "1.2.3"));
    }

    @Test
    void testSimpleFormat() {
        assertEquals("1.2.3", formatter.format(SIMPLE_FORMAT, NUMERIC_VERSION_FACTORY.create("1.2.3")));
        assertEquals("MyComponent.1.2.3", formatter.format(COMPONENT_FORMAT, NUMERIC_VERSION_FACTORY.create("1.2.3")));
        assertEquals("01.4000.00.444", formatter.format("$major02.${minor02}.$service02.$fix02", NUMERIC_VERSION_FACTORY.create("1.4000.0.444")));

        assert formatter.matchesFormat(SIMPLE_FORMAT, "1.2.3");
        assert !formatter.matchesFormat(SIMPLE_FORMAT, "1.2.3-4");

        assert formatter.matchesFormat(COMPONENT_FORMAT, "MyComponent.1.2.34");
        assert formatter.matchesFormat("MyComponent.$major.$minor.$service", "MyComponent.1.2.34");
        assert formatter.matchesFormat("MyComponent-$major.$minor.$service", "MyComponent-1.2.34");
        assert !formatter.matchesFormat("MyComponent.$major.$minor.$service", "MyComponent2.1.2.34");

        assert formatter.matchesFormat("$major02.$minor02.$service02.$fix02", "00.02.34.23");
    }

    @Test
    void testNonStrictMatcher() {
        assertFalse(formatter.matchesNonStrictFormat(BUILD_VERSION_FORMAT, "1.2.3"));
        assertTrue(formatter.matchesNonStrictFormat(BUILD_VERSION_FORMAT, "1.2.3-4-5"));
        assertTrue(formatter.matchesNonStrictFormat(BUILD_VERSION_FORMAT, "1.2.3.4.5"));
        assertTrue(formatter.matchesNonStrictFormat(BUILD_VERSION_FORMAT, "1.2.3.4.5-6"));
    }

    @Test
    void testBuildFormat() {
        assertEquals("1.2.3.4-5", formatter.format(BUILD_VERSION_FORMAT, NUMERIC_VERSION_FACTORY.create("1.2.3.4.5")));
    }

    @Test
    void testBranchFormat() {
        assertEquals("C_03_44_30", formatter.format(BRANCH_FORMAT, NUMERIC_VERSION_FACTORY.create("3.44.29.2-2")));
        assertEquals("C_03_44_30", formatter.format(BRANCH_FORMAT_WITH_BRACES, NUMERIC_VERSION_FACTORY.create("3.44.29.2-2")));
        assertEquals("C_03_44_30", formatter.format(BRANCH_FORMAT, NUMERIC_VERSION_FACTORY.create("3.44.30.2-2")));
        assertEquals("C_03_44_30", formatter.format(BRANCH_FORMAT, NUMERIC_VERSION_FACTORY.create("03.44.31.15-35"))); //-> major
    }

    @Test
    void testSimpleCustomerFormat() {
        String customerVersion = formatter.formatToCustomerVersion("$versionPrefix-$baseVersionFormat",
                SIMPLE_FORMAT,
                "halyk", NUMERIC_VERSION_FACTORY.create("1.2.3"));
        assertEquals("halyk-1.2.3", customerVersion);

        customerVersion = formatter.formatToCustomerVersion("$versionPrefix.$baseVersionFormat", SIMPLE_FORMAT2, "akBARS", NUMERIC_VERSION_FACTORY.create("1.2.3.4"));
        assertEquals("akBARS.1.2.3-4", customerVersion);
    }

    @Test
    void testHotfixCustomerFormat() {
        String customerVersion = formatter.formatToCustomerVersion(
                "$versionPrefix-$baseVersionFormat-$hotfixSuffix",
                "$major.$minor.$service",
                "halyk",
                "HOTFIX$counter",
                    NUMERIC_VERSION_FACTORY.create("1.2.3.4-5"));
        assertEquals("halyk-1.2.3-HOTFIX5", customerVersion);
    }

    @Test
    void testMatchesVersionFormat() {
        assertTrue(formatter.matchesFormat(FORMAT, "02.03.35"));
        assertTrue(formatter.matchesFormat("$versionPrefix.$baseVersionFormat", FORMAT, "akBARS", "akBARS.02.03.35"));
    }

    @Test
    void testMajorCWithMajor() {
        assert formatter.matchesFormat("$major.$minorC.$service", "3.38.3");
        assert formatter.matchesFormat("$major.$minorC.$serviceC", "3.38.04");
    }

    @Test
    void testMatchesCustomerFormat() {
        assertTrue(formatter.matchesFormat("$versionPrefix-$baseVersionFormat-$hotfixSuffix", SIMPLE_FORMAT, "halyk", "hf", "halyk-1.2.3-hf"),
                "Hotfix version must match format with hotfix suffix");
        assertFalse(formatter.matchesFormat("$versionPrefix-$baseVersionFormat-$hotfixSuffix", SIMPLE_FORMAT, "halyk", "hf$fix", "halyk-1.2.3-hf"),
                "Hotfix version not matches format with incorrect hotfix suffix");

        assertTrue(formatter.matchesFormat("$versionPrefix-$baseVersionFormat", SIMPLE_FORMAT, "halyk", "halyk-1.2.3"));
        assertFalse(formatter.matchesFormat("$versionPrefix.$baseVersionFormat", SIMPLE_FORMAT, "halyk", "halyk-1.2.3"));

        assertTrue(formatter.matchesFormat("$versionPrefix.$baseVersionFormat", SIMPLE_FORMAT2, "akBARS", "akBARS.1.2.3-4"));
        assertFalse(formatter.matchesFormat("$versionPrefix.$baseVersionFormat", SIMPLE_FORMAT, "akBARS", "akBARS.1.2.3-4"));
    }

    @Test
    void testMatches4DigitFormat() {
        assertTrue(formatter.matchesFormat("$major.$minor.$service.$fix02", "1.9.128.06"));
        assertFalse(formatter.matchesFormat("$major.$minor.$service.$fix", "1.9.128.06"));
    }
}
