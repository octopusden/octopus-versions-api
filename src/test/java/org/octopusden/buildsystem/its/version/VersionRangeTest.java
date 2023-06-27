package org.octopusden.buildsystem.its.version;

import org.octopusden.releng.versions.IVersionInfo;
import org.octopusden.releng.versions.NumericVersion;
import org.octopusden.releng.versions.VersionNames;
import org.octopusden.releng.versions.VersionRange;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class VersionRangeTest {

    private static final VersionNames VERSION_NAMES = new VersionNames("serviceCBranch", "serviceC", "minorC");

    @Test
    void testHardEqual() {
        assertTrue(VersionRange.createFromVersionSpec("[0]", VERSION_NAMES).containsVersion(NumericVersion.parse("0", VERSION_NAMES)));
        assertTrue(VersionRange.createFromVersionSpec("[1]", VERSION_NAMES).containsVersion(NumericVersion.parse("1", VERSION_NAMES)));
        assertTrue(VersionRange.createFromVersionSpec("[03.49.30-999]", VERSION_NAMES).containsVersion(NumericVersion.parse("03.49.30-999", VERSION_NAMES)));
        assertFalse(VersionRange.createFromVersionSpec("[03.49.30-999]", VERSION_NAMES).containsVersion(NumericVersion.parse("03.49.30-998", VERSION_NAMES)));
        assertFalse(VersionRange.createFromVersionSpec("[03.49.30-999]", VERSION_NAMES).containsVersion(NumericVersion.parse("03.49.30-1000", VERSION_NAMES)));
    }

    @Test
    void testMultiplyRanges() {
        assertTrue(VersionRange.createFromVersionSpec("(, 03.49.30-999],(03.49.30-999,03.49.30-1000]", VERSION_NAMES).containsVersion(NumericVersion.parse("03.49.30.269", VERSION_NAMES)));
        assertFalse(VersionRange.createFromVersionSpec("(, 03.49.30-999],(03.49.30-999,03.49.30-1000]", VERSION_NAMES).containsVersion(NumericVersion.parse("03.49.30.1001", VERSION_NAMES)));
        assertTrue(VersionRange.createFromVersionSpec("(, 03.49.30-999],(03.49.30-999,03.49.30-1000]", VERSION_NAMES).containsVersion(NumericVersion.parse("03.49.30.1000", VERSION_NAMES)));
        assertFalse(VersionRange.createFromVersionSpec("(03.49.30-269, 03.49.30-999],(03.49.30-999,03.49.30-1000]", VERSION_NAMES).containsVersion(NumericVersion.parse("03.49.30.269", VERSION_NAMES)));
    }

    @Test
    void testDifferentVersionFormats() {
        assertTrue(VersionRange.createFromVersionSpec("(, 03.49.30-999]", VERSION_NAMES).containsVersion(NumericVersion.parse("03.49.30.269", VERSION_NAMES)));
        assertFalse(VersionRange.createFromVersionSpec("(, 03.49.30-999]", VERSION_NAMES).containsVersion(NumericVersion.parse("03.49.30.1000", VERSION_NAMES)));
        assertTrue(VersionRange.createFromVersionSpec("[03.49.30-269, 03.49.30-999]", VERSION_NAMES).containsVersion(NumericVersion.parse("03.49.30.269", VERSION_NAMES)));
        assertTrue(VersionRange.createFromVersionSpec("(03.49.30-268, 03.49.30-999]", VERSION_NAMES).containsVersion(NumericVersion.parse("03.49.30.269", VERSION_NAMES)));
        assertFalse(VersionRange.createFromVersionSpec("(03.49.30-269, 03.49.30-999]", VERSION_NAMES).containsVersion(NumericVersion.parse("03.49.30.269", VERSION_NAMES)));

        assertTrue(VersionRange.createFromVersionSpec("(, 03.49.30.999)", VERSION_NAMES).containsVersion(NumericVersion.parse("03.49.30-269", VERSION_NAMES)));
        assertFalse(VersionRange.createFromVersionSpec("(, 03.49.30.999)", VERSION_NAMES).containsVersion(NumericVersion.parse("03.49.30-1000", VERSION_NAMES)));
    }

    @Test
    void test4DigitVersionRange() {
        final IVersionInfo version = NumericVersion.parse("03.49.30.269", VERSION_NAMES);
        assertTrue(VersionRange.createFromVersionSpec("(, 03.49.30.999]", VERSION_NAMES).containsVersion(version));
        assertTrue(VersionRange.createFromVersionSpec("(, 03.49.30.999)", VERSION_NAMES).containsVersion(version));
        assertTrue(VersionRange.createFromVersionSpec("(03.49.30, 03.49.30.9999)", VERSION_NAMES).containsVersion(version));
        assertTrue(VersionRange.createFromVersionSpec("(03.49.30.261, 03.49.30.999)", VERSION_NAMES).containsVersion(version));
        assertFalse(VersionRange.createFromVersionSpec("(03.49.30.00, 03.49.30.261]", VERSION_NAMES).containsVersion(version));
        assertFalse(VersionRange.createFromVersionSpec("(03.49.30.00, 03.49.30.269)", VERSION_NAMES).containsVersion(version));

        assertTrue(VersionRange.createFromVersionSpec("(03.49.30.00-123, 03.49.30.270-0545)", VERSION_NAMES).containsVersion(version));
        assertTrue(VersionRange.createFromVersionSpec("(3.49.30.00-123, 3.49.30.270-0545)", VERSION_NAMES).containsVersion(NumericVersion.parse("3.49.30.269", VERSION_NAMES)));
        assertTrue(VersionRange.createFromVersionSpec("(3.49.30.00-123, 3.49.30.270-0545)", VERSION_NAMES).containsVersion(NumericVersion.parse("3.49.30.269-22", VERSION_NAMES)));
        assertTrue(VersionRange.createFromVersionSpec("(3.49.30.00-123, 3.49.32.270-0545)", VERSION_NAMES).containsVersion(NumericVersion.parse("3.49.31", VERSION_NAMES)));
        assertFalse(VersionRange.createFromVersionSpec("(3.49.30.00-123, 3.49.32.270-0545)", VERSION_NAMES).containsVersion(NumericVersion.parse("3.49.33", VERSION_NAMES)));
        assertTrue(VersionRange.createFromVersionSpec("(3.49.30.00-123, 3.49.30.270-0545)", VERSION_NAMES).containsVersion(version));
        assertFalse(VersionRange.createFromVersionSpec("(3.49.30.00-123, 3.49.30.268-0545)", VERSION_NAMES).containsVersion(version));
    }

    @Test
    void testVersionRange()  {
        IVersionInfo version1 = NumericVersion.parse("1.13.2-15", VERSION_NAMES);
        IVersionInfo version2 = NumericVersion.parse("1.12.2-15", VERSION_NAMES);
        IVersionInfo version3 = NumericVersion.parse("1.1", VERSION_NAMES);
        IVersionInfo version4 = NumericVersion.parse("1.10", VERSION_NAMES);
        IVersionInfo version5 = NumericVersion.parse("1.12.1-151", VERSION_NAMES);
        IVersionInfo version6 = NumericVersion.parse("2", VERSION_NAMES);
        IVersionInfo version7 = NumericVersion.parse("1", VERSION_NAMES);

        VersionRange versionRange = VersionRange.createFromVersionSpec("[1.12.1-150,)", VERSION_NAMES);

        assertTrue(versionRange.containsVersion(version1));
        assertTrue(versionRange.containsVersion(version2));
        assertFalse(versionRange.containsVersion(version3));
        assertFalse(versionRange.containsVersion(version4));
        assertTrue(versionRange.containsVersion(version5));
        assertTrue(versionRange.containsVersion(version6));
        assertFalse(versionRange.containsVersion(version7));
    }

    @Test
    void testCommonCase() {
        assertThat("(,3),(3,)", notContainAnyVersion("3"));
        assertThat("(,3),(3,)", containAllVersions("2", "4"));
        VersionRange versionRange = VersionRange.createFromVersionSpec("[3.49.29.18, 4.0)", VERSION_NAMES);
        IVersionInfo version = NumericVersion.parse("3.49.29.19.10", VERSION_NAMES);
        assertTrue(versionRange.containsVersion(version));
    }

    // RELENG-109
    @Test
    void testVersionWithZeroInTheMiddle() {
        IVersionInfo version = NumericVersion.parse("10.0.8", VERSION_NAMES);
        VersionRange versionRange = VersionRange.createFromVersionSpec("(10,)", VERSION_NAMES);
        assertTrue(versionRange.containsVersion(version));
        assertFalse(versionRange.containsVersion(NumericVersion.parse("10.0", VERSION_NAMES)));
    }

    //Below tests are ported from maven source maven-artifact/src/test/java/org/apache/maven/artifact/versioning/VersionRangeTest.java

    @ParameterizedTest
    @ValueSource(strings  = {"(1.0)", "[1.0)", "(1.0]", "(1.0,1.0]", "[1.0,1.0)", "(1.0,1.0)", "[1.1,1.0]", "[1.0,1.2),1.3"})
    void testInvalidRangeSpecification(final String rangeSpecification) {
        assertThrows(IllegalArgumentException.class, ()-> VersionRange.createFromVersionSpec(rangeSpecification, VERSION_NAMES));
    }

    @ParameterizedTest
    @ValueSource(strings  = {"[1.0,1.2),(1.1,1.3]", "[1.1,1.3),(1.0,1.2]"})
    void testInvalidRangeSpecificationDueToTheOverlap(final String rangeSpecification) {
        assertThrows(IllegalArgumentException.class, ()-> VersionRange.createFromVersionSpec(rangeSpecification, VERSION_NAMES));
    }

    @ParameterizedTest
    @ValueSource(strings  = {"(1.1,1.2],[1.0,1.1)", "(3, 4],[1, 3)"})
    void testInvalidRangeSpecificationDueToTheOrdering(final String rangeSpecification) {
        assertThrows(IllegalArgumentException.class, ()-> VersionRange.createFromVersionSpec(rangeSpecification, VERSION_NAMES));
    }

    @ParameterizedTest
    @MethodSource("positiveIntersectionData")
    void testPositiveIntersection(final String leftRange, final String rightRange) {
        final VersionRange leftVersionRange = VersionRange.createFromVersionSpec(leftRange, VERSION_NAMES);
        final VersionRange rightVersionRange = VersionRange.createFromVersionSpec(rightRange, VERSION_NAMES);
        assertTrue(leftVersionRange.isIntersect(rightVersionRange));
        assertTrue(rightVersionRange.isIntersect(leftVersionRange));
    }

    private static Stream<Arguments> positiveIntersectionData() {
        return Stream.of(
                Arguments.of("[1, 4]", "(1, 4)"),
                Arguments.of("[1, 4]", "[0, 3]"),
                Arguments.of("[1, 4]", "[2, 5]"),
                Arguments.of("[1, 4]", "[2, 3]"),
                Arguments.of("(1, 3]", "(, 2)"),
                Arguments.of("(1, 3]", "(, 3)"),
                Arguments.of("(1, 3]", "(, 4]"),
                Arguments.of("(, 3]", "(, 4]"),
                Arguments.of("(, 3)", "(, 4]"),
                Arguments.of("(, 3]", "(, 4)"),
                Arguments.of("(, 3)", "(, 4)"),
                Arguments.of("(3, )", "(4, )"),
                Arguments.of("[3, )", "(4, )"),
                Arguments.of("(4, )", "(4, )"),
                Arguments.of("(5, )", "(4, )"),
                Arguments.of("(, 3)", "(, 3)"),
                Arguments.of("(, 3)", "(, 3]"),
                Arguments.of("(3, )", "(3, )"),
                Arguments.of("(3, )", "[3, )"),
                Arguments.of("(, 3]", "[3, )"),
                Arguments.of("[3]", "[3, )"),
                Arguments.of("[3]", "(,3]"),
                Arguments.of("[3]", "[3]")
        );
    }

    @ParameterizedTest
    @MethodSource("negativeIntersectionData")
    void testNegativeIntersection(final String leftRange, final String rightRange) {
        final VersionRange leftVersionRange = VersionRange.createFromVersionSpec(leftRange, VERSION_NAMES);
        final VersionRange rightVersionRange = VersionRange.createFromVersionSpec(rightRange, VERSION_NAMES);
        assertFalse(leftVersionRange.isIntersect(rightVersionRange));
        assertFalse(rightVersionRange.isIntersect(leftVersionRange));
    }

    private static Stream<Arguments> negativeIntersectionData() {
        return Stream.of(
                Arguments.of("(1, 2)", "(3, 4)"),
                Arguments.of("[1, 4]", "[5, 7]"),
                Arguments.of("[1, 4]", "(4, 7]"),
                Arguments.of("[1, 4)", "[4, 7]"),
                Arguments.of("(, 4]", "[5, 7]"),
                Arguments.of("[1, 4]", "[5, )"),
                Arguments.of("(, 4]", "[5, )"),
                Arguments.of("(, 4]", "(4, 7]"),
                Arguments.of("(, 4)", "[4, 7]"),
                Arguments.of("[1, 4]", "(4, )"),
                Arguments.of("[1, 4)", "[4, )"),
                Arguments.of("(, 4]", "(4, )"),
                Arguments.of("(, 4)", "[4, )"),
                Arguments.of("[1.6.2410]", "[1.6,1.6.2410),(1.6.2410,1.7)"),
                Arguments.of("[3]", "(3, )"),
                Arguments.of("[3]", "(,3)"),
                Arguments.of("[1]", "[2]")
        );
    }

    @Test
    void testRangeFormMavenTest() {
        assertThat("[5.0.9.0,5.0.10.0)", containAllVersions("5.0.9.0"));
        assertThat("[5.0.9.0,5.0.10.0)", notContainAnyVersion("5.0.8", "5.0.10"));
    }

    @Test
    void testContainsFormMavenTest() {
        assertThat("[2.0.5]", containAllVersions("2.0.5"));
        assertThat("[2.0,2.1]", containAllVersions("2.0.5"));
        assertThat("[2.0,2.0.5]", containAllVersions("2.0.5"));

        assertThat("[2.0.6,)", notContainAnyVersion("2.0.5"));
        assertThat("[2.0.6]", notContainAnyVersion("2.0.5"));
        assertThat("[2.0,2.0.3]", notContainAnyVersion("2.0.5"));
        assertThat("[2.0,2.0.5)", notContainAnyVersion("2.0.5"));
    }

    private static class VersionRangeContainsMatcher extends TypeSafeMatcher<String> {
        private final boolean contains;
        private final String[] versions;
        private final Collection<String> descriptorVersions = new ArrayList<>();

        private VersionRangeContainsMatcher(final boolean contains, final String... versions) {
            this.contains = contains;
            this.versions = versions;
        }

        @Override
        protected boolean matchesSafely(final String rangeSpecification) {
            final VersionRange versionRange = VersionRange.createFromVersionSpec(rangeSpecification, VERSION_NAMES);
            final Collection<String> containAnyCollection = new ArrayList<>();
            final Collection<String> notContainCollection = new ArrayList<>();
            for (final String version: versions) {
                if (!versionRange.containsVersion(NumericVersion.parse(version, VERSION_NAMES))) {
                    notContainCollection.add(version);
                } else {
                    containAnyCollection.add(version);
                }
            }
            if (contains) {
                descriptorVersions.addAll(notContainCollection);
                return notContainCollection.isEmpty();
            }
            descriptorVersions.addAll(containAnyCollection);
            return containAnyCollection.isEmpty();
        }

        @Override
        public void describeTo(Description description) {
            if (contains) {
                description.appendText("not contain: ");
            } else {
                description.appendText("contains: ");
            }
            description.appendText(String.join(",", descriptorVersions));
        }
    }

    private static Matcher<String> containAllVersions(final String... versions) {
        return new VersionRangeContainsMatcher(true, versions);
    }

    private static Matcher<String> notContainAnyVersion(final String... versions) {
        return new VersionRangeContainsMatcher(false, versions);
    }
}
