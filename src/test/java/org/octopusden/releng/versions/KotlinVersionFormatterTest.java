package org.octopusden.releng.versions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KotlinVersionFormatterTest {

    private static final String SIMPLE_FORMAT = "$major.$minor.$service";
    private static final String SIMPLE_FORMAT2 = "$major.$minor.$service-$fix";
    private static final String BUILD_VERSION_FORMAT = "$major.$minor.$service.$fix-$build";
    private static final String COMPONENT_FORMAT = "MyComponent.$major.$minor.$service";
    private static final String CARDS_BRANCH_FORMAT = "CARDS_$major02_$minorCards_$serviceCardsBranch";
    private static final String CARDS_BRANCH_FORMAT_WITH_BRACES = "CARDS_${major02}_$minorCards_$serviceCardsBranch";
    private static final String CARDS_FORMAT = "$major02.$minorCards.$serviceCards";

    private VersionFormatter formatter = new KotlinVersionFormatter();

    @Test
    void testPostProcessorList() {
        assertEquals("1-2-3", new KotlinVersionFormatter().getPREDEFINED_POSTPROCESSOR_LIST().get(3).component2().invoke("", "1.2.3"));
    }

    @Test
    void testSimpleFormat() {
        assertEquals("1.2.3", formatter.format(SIMPLE_FORMAT, NumericVersion.parse("1.2.3")));
        assertEquals("MyComponent.1.2.3", formatter.format(COMPONENT_FORMAT, NumericVersion.parse("1.2.3")));
        assertEquals("01.4000.00.444", formatter.format("$major02.${minor02}.$service02.$fix02", NumericVersion.parse("1.4000.0.444")));

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
        assertEquals("1.2.3.4-5", formatter.format(BUILD_VERSION_FORMAT, NumericVersion.parse("1.2.3.4.5")));
    }

    @Test
    void testCardsBranchFormat() {
        assertEquals("CARDS_03_44_30", formatter.format(CARDS_BRANCH_FORMAT, NumericVersion.parse("3.44.29.2-2")));
        assertEquals("CARDS_03_44_30", formatter.format(CARDS_BRANCH_FORMAT_WITH_BRACES, NumericVersion.parse("3.44.29.2-2")));
        assertEquals("CARDS_03_44_30", formatter.format(CARDS_BRANCH_FORMAT, NumericVersion.parse("3.44.30.2-2")));
        assertEquals("CARDS_03_44_30", formatter.format(CARDS_BRANCH_FORMAT, NumericVersion.parse("03.44.31.15-35"))); //-> major
    }

    @Test
    void testSimpleCustomerFormat() {
        String customerVersion = formatter.formatToCustomerVersion("$versionPrefix-$baseVersionFormat", SIMPLE_FORMAT,
                "halyk", NumericVersion.parse("1.2.3"));
        assertEquals("halyk-1.2.3", customerVersion);

        customerVersion = formatter.formatToCustomerVersion("$versionPrefix.$baseVersionFormat", SIMPLE_FORMAT2, "akBARS", NumericVersion.parse("1.2.3.4"));
        assertEquals("akBARS.1.2.3-4", customerVersion);
    }

    @Test
    void testMatchesCardsVersionFormat() {
        assertTrue(formatter.matchesFormat(CARDS_FORMAT, "02.03.35"));
        assertTrue(formatter.matchesFormat("$versionPrefix.$baseVersionFormat", CARDS_FORMAT, "akBARS", "akBARS.02.03.35"));
    }

    @Test
    void testMajorCardsWithMajor() {
        assert formatter.matchesFormat("$major.$minorCards.$service", "3.38.3");
        assert formatter.matchesFormat("$major.$minorCards.$serviceCards", "3.38.04");
    }

    @Test
    void testMatchesCustomerFormat() {
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
