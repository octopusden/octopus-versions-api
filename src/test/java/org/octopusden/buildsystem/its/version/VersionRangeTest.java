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
    private static final VersionRange.Builder VERSION_RANGE_BUILDER = new VersionRange.Builder(VERSION_NAMES);
    private static final NumericVersion.Builder NUMERIC_VERSION_BUILDER = new NumericVersion.Builder(VERSION_NAMES);

    @Test
    void testHardEqual() {
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("[0]").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("0").build()));
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("[1]").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("1").build()));
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("[03.49.30-999]").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30-999").build()));
        assertFalse(VERSION_RANGE_BUILDER.setVersionRange("[03.49.30-999]").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30-998").build()));
        assertFalse(VERSION_RANGE_BUILDER.setVersionRange("[03.49.30-999]").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30-1000").build()));
    }

    @Test
    void testMultiplyRanges() {
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("(, 03.49.30-999],(03.49.30-999,03.49.30-1000]").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30.269").build()));
        assertFalse(VERSION_RANGE_BUILDER.setVersionRange("(, 03.49.30-999],(03.49.30-999,03.49.30-1000]").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30.1001").build()));
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("(, 03.49.30-999],(03.49.30-999,03.49.30-1000]").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30.1000").build()));
        assertFalse(VERSION_RANGE_BUILDER.setVersionRange("(03.49.30-269, 03.49.30-999],(03.49.30-999,03.49.30-1000]").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30.269").build()));
    }

    @Test
    void testDifferentVersionFormats() {
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("(, 03.49.30-999]").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30.269").build()));
        assertFalse(VERSION_RANGE_BUILDER.setVersionRange("(, 03.49.30-999]").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30.1000").build()));
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("[03.49.30-269, 03.49.30-999]").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30.269").build()));
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("(03.49.30-268, 03.49.30-999]").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30.269").build()));
        assertFalse(VERSION_RANGE_BUILDER.setVersionRange("(03.49.30-269, 03.49.30-999]").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30.269").build()));

        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("(, 03.49.30.999)").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30-269").build()));
        assertFalse(VERSION_RANGE_BUILDER.setVersionRange("(, 03.49.30.999)").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30-1000").build()));
    }

    @Test
    void test4DigitVersionRange() {
        final IVersionInfo version = NUMERIC_VERSION_BUILDER.setRawVersion("03.49.30.269").build();
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("(, 03.49.30.999]").build().containsVersion(version));
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("(, 03.49.30.999)").build().containsVersion(version));
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("(03.49.30, 03.49.30.9999)").build().containsVersion(version));
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("(03.49.30.261, 03.49.30.999)").build().containsVersion(version));
        assertFalse(VERSION_RANGE_BUILDER.setVersionRange("(03.49.30.00, 03.49.30.261]").build().containsVersion(version));
        assertFalse(VERSION_RANGE_BUILDER.setVersionRange("(03.49.30.00, 03.49.30.269)").build().containsVersion(version));

        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("(03.49.30.00-123, 03.49.30.270-0545)").build().containsVersion(version));
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("(3.49.30.00-123, 3.49.30.270-0545)").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("3.49.30.269").build()));
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("(3.49.30.00-123, 3.49.30.270-0545)").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("3.49.30.269-22").build()));
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("(3.49.30.00-123, 3.49.32.270-0545)").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("3.49.31").build()));
        assertFalse(VERSION_RANGE_BUILDER.setVersionRange("(3.49.30.00-123, 3.49.32.270-0545)").build().containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("3.49.33").build()));
        assertTrue(VERSION_RANGE_BUILDER.setVersionRange("(3.49.30.00-123, 3.49.30.270-0545)").build().containsVersion(version));
        assertFalse(VERSION_RANGE_BUILDER.setVersionRange("(3.49.30.00-123, 3.49.30.268-0545)").build().containsVersion(version));
    }

    @Test
    void testVersionRange()  {
        IVersionInfo version1 = NUMERIC_VERSION_BUILDER.setRawVersion("1.13.2-15").build();
        IVersionInfo version2 = NUMERIC_VERSION_BUILDER.setRawVersion("1.12.2-15").build();
        IVersionInfo version3 = NUMERIC_VERSION_BUILDER.setRawVersion("1.1").build();
        IVersionInfo version4 = NUMERIC_VERSION_BUILDER.setRawVersion("1.10").build();
        IVersionInfo version5 = NUMERIC_VERSION_BUILDER.setRawVersion("1.12.1-151").build();
        IVersionInfo version6 = NUMERIC_VERSION_BUILDER.setRawVersion("2").build();
        IVersionInfo version7 = NUMERIC_VERSION_BUILDER.setRawVersion("1").build();

        VersionRange versionRange = VERSION_RANGE_BUILDER.setVersionRange("[1.12.1-150,)").build();

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
        VersionRange versionRange = VERSION_RANGE_BUILDER.setVersionRange("[3.49.29.18, 4.0)").build();
        IVersionInfo version = NUMERIC_VERSION_BUILDER.setRawVersion("3.49.29.19.10").build();
        assertTrue(versionRange.containsVersion(version));
    }

    // RELENG-109
    @Test
    void testVersionWithZeroInTheMiddle() {
        IVersionInfo version = NUMERIC_VERSION_BUILDER.setRawVersion("10.0.8").build();
        VersionRange versionRange = VERSION_RANGE_BUILDER.setVersionRange("(10,)").build();
        assertTrue(versionRange.containsVersion(version));
        assertFalse(versionRange.containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion("10.0").build()));
    }

    //Below tests are ported from maven source maven-artifact/src/test/java/org/apache/maven/artifact/versioning/VersionRangeTest.java

    @ParameterizedTest
    @ValueSource(strings  = {"(1.0)", "[1.0)", "(1.0]", "(1.0,1.0]", "[1.0,1.0)", "(1.0,1.0)", "[1.1,1.0]", "[1.0,1.2),1.3"})
    void testInvalidRangeSpecification(final String rangeSpecification) {
        assertThrows(IllegalArgumentException.class, ()-> VERSION_RANGE_BUILDER.setVersionRange(rangeSpecification));
    }

    @ParameterizedTest
    @ValueSource(strings  = {"[1.0,1.2),(1.1,1.3]", "[1.1,1.3),(1.0,1.2]"})
    void testInvalidRangeSpecificationDueToTheOverlap(final String rangeSpecification) {
        assertThrows(IllegalArgumentException.class, ()-> VERSION_RANGE_BUILDER.setVersionRange(rangeSpecification));
    }

    @ParameterizedTest
    @ValueSource(strings  = {"(1.1,1.2],[1.0,1.1)", "(3, 4],[1, 3)"})
    void testInvalidRangeSpecificationDueToTheOrdering(final String rangeSpecification) {
        assertThrows(IllegalArgumentException.class, ()-> VERSION_RANGE_BUILDER.setVersionRange(rangeSpecification));
    }

    @ParameterizedTest
    @MethodSource("positiveIntersectionData")
    void testPositiveIntersection(final String leftRange, final String rightRange) {
        final VersionRange leftVersionRange = VERSION_RANGE_BUILDER.setVersionRange(leftRange).build();
        final VersionRange rightVersionRange = VERSION_RANGE_BUILDER.setVersionRange(rightRange).build();
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
        final VersionRange leftVersionRange = VERSION_RANGE_BUILDER.setVersionRange(leftRange).build();
        final VersionRange rightVersionRange = VERSION_RANGE_BUILDER.setVersionRange(rightRange).build();
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
            final VersionRange versionRange = VERSION_RANGE_BUILDER.setVersionRange(rangeSpecification).build();
            final Collection<String> containAnyCollection = new ArrayList<>();
            final Collection<String> notContainCollection = new ArrayList<>();
            for (final String version: versions) {
                if (!versionRange.containsVersion(NUMERIC_VERSION_BUILDER.setRawVersion(version).build())) {
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
