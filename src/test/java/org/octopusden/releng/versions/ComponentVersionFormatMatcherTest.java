package org.octopusden.releng.versions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComponentVersionFormatMatcherTest {

    private static final ComponentVersionFormatMatcher matcher = new ComponentVersionFormatMatcher(new VersionNames("serviceCBranch", "serviceC", "minorC"));
    private ComponentVersionFormat MODEL_VERSION_FORMAT = ComponentVersionFormat.create(
            "Model.$major.$minor.$service",
            "Model.$major.$minor.$service.$fix",
            "Model.$major.$minor.$service.$fix",
            "Model.$major02.$minor02",
            "Model.$major.$minor.$service.$fix-$build");
    private ComponentVersionFormat VERSION_FORMAT = ComponentVersionFormat.create(
            "$major02.$minorC.$serviceC",
            "$major02.$minor02.$service02.$fix02",
            "$major02.$minor02.$service02.$fix02",
            "$major02.$minor02",
            "$major02.$minor02.$service02.$fix02-$build");
    private ComponentVersionFormat SIMPLE_VERSION_FORMAT = ComponentVersionFormat.create(
            "$major.$minor",
            "$major.$minor.$service",
            "$major.$minor.$service.$fix",
            "$major02.$minor02",
            "$major.$minor.$service.$fix-$build");

    @Test
    void testMatchesVersionFormat() {
        assertTrue(matcher.matchesMajorVersionFormat(SIMPLE_VERSION_FORMAT, "1.2"));
        assertFalse(matcher.matchesMajorVersionFormat(SIMPLE_VERSION_FORMAT, "1.2.3"));

        assertTrue(matcher.matchesReleaseVersionFormat(SIMPLE_VERSION_FORMAT, "1.2.3"));
        assertFalse(matcher.matchesReleaseVersionFormat(SIMPLE_VERSION_FORMAT, "1.2"));

        assertTrue(matcher.matchesHotfixVersionFormat(SIMPLE_VERSION_FORMAT, "1.2.3.4-5"));
        assertFalse(matcher.matchesHotfixVersionFormat(SIMPLE_VERSION_FORMAT, "1.2.3"));
    }
    @Test
    void testMatchesMajorVersionFormat() {
        assertTrue(matcher.matchesMajorVersionFormat(MODEL_VERSION_FORMAT, "Model.1.2.3"));
    }

    @Test
    void testWrongPrefixInMajorVersionFormat() {
        assertFalse(matcher.matchesMajorVersionFormat(MODEL_VERSION_FORMAT, "Modeler.1.2.3"));
    }

    @Test
    void testWrongVersionMajorFormat() {
        assertFalse(matcher.matchesMajorVersionFormat(MODEL_VERSION_FORMAT, "Model.1.2"));
    }

    @Test
    void testMatchesReleaseVersionFormat() {
        assertTrue(matcher.matchesReleaseVersionFormat(MODEL_VERSION_FORMAT, "Model.1.2.3.4"));
    }

    @Test
    void testWrongPrefixInReleaseVersionFormat() {
        assertFalse(matcher.matchesReleaseVersionFormat(MODEL_VERSION_FORMAT, "Modeler.1.2.3.4"));
    }

    @Test
    void testWrongVersionReleaseFormat() {
        assertFalse(matcher.matchesReleaseVersionFormat(MODEL_VERSION_FORMAT, "Model.1.2.3"));
    }

    @Test
    void testMatchesHotfixVersionFormat() {
        assertTrue(matcher.matchesHotfixVersionFormat(MODEL_VERSION_FORMAT, "Model.1.2.3.4-5"));
    }

    @Test
    void testMatchesBuildVersionFormat() {
        assertTrue(matcher.matchesBuildVersionFormat(MODEL_VERSION_FORMAT, "Model.1.2.3.4"));
    }

    @Test
    void testWrongPrefixInBuildVersionFormat() {
        assertFalse(matcher.matchesBuildVersionFormat(MODEL_VERSION_FORMAT, "Modeler.1.2.3.4"));
    }

    @Test
    void testWrongVersionBuildFormat() {
        assertFalse(matcher.matchesBuildVersionFormat(MODEL_VERSION_FORMAT, "Model.1.2.3.4.5"));
    }

    @Test
    void testWrongVersionHotfixFormat() {
        assertFalse(matcher.matchesHotfixVersionFormat(MODEL_VERSION_FORMAT, "Model.1.2.3.4.5"));
    }

    @Test
    void testFormatNotStrictMatch() {
        assertTrue(matcher.matchesBuildVersionFormatNonStrict(MODEL_VERSION_FORMAT, "2.0.1.2.0"));
        assertFalse(matcher.matchesBuildVersionFormatNonStrict(MODEL_VERSION_FORMAT, "2.0.1"));

        assertTrue(matcher.matchesReleaseVersionFormatNonStrict(MODEL_VERSION_FORMAT, "2.0.1.0"));
        assertFalse(matcher.matchesReleaseVersionFormatNonStrict(MODEL_VERSION_FORMAT, "2.0.1"));

        assertTrue(matcher.matchesMajorVersionFormatNonStrict(MODEL_VERSION_FORMAT, "2.0.1"));
        assertFalse(matcher.matchesMajorVersionFormatNonStrict(MODEL_VERSION_FORMAT, "2.0"));

        assertTrue(matcher.matchesLineVersionFormatNonStrict(MODEL_VERSION_FORMAT, "2.0"));
        assertFalse(matcher.matchesLineVersionFormatNonStrict(MODEL_VERSION_FORMAT, "2"));

        assertTrue(matcher.matchesHotfixVersionFormatNonStrict(MODEL_VERSION_FORMAT, "2.0.1.0-1"));
        assertFalse(matcher.matchesHotfixVersionFormatNonStrict(MODEL_VERSION_FORMAT, "2.0.1"));
    }

    @Test
    void testVersionFormatNotStrictMatch() {
        assertTrue(matcher.matchesMajorVersionFormatNonStrict(VERSION_FORMAT, "03.40.29"));
        assertFalse(matcher.matchesMajorVersionFormatNonStrict(VERSION_FORMAT, "03.02"));

        assertFalse(matcher.matchesBuildVersionFormatNonStrict(MODEL_VERSION_FORMAT, "03.40.30"));
        assertTrue(matcher.matchesBuildVersionFormatNonStrict(MODEL_VERSION_FORMAT, "03.40.30.29"));

        assertFalse(matcher.matchesReleaseVersionFormatNonStrict(MODEL_VERSION_FORMAT, "03.39.49"));
        assertTrue(matcher.matchesReleaseVersionFormatNonStrict(MODEL_VERSION_FORMAT, "4.54.3.23"));

        assertFalse(matcher.matchesLineVersionFormatNonStrict(MODEL_VERSION_FORMAT, "03"));
        assertTrue(matcher.matchesLineVersionFormatNonStrict(MODEL_VERSION_FORMAT, "4.54.39"));
    }
}
