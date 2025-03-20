package org.octopusden.releng.versions;

public final class ComponentVersionFormatMatcher {

    private final KotlinVersionFormatter formatter;


    public ComponentVersionFormatMatcher(VersionNames versionNames) {
        formatter = new KotlinVersionFormatter(versionNames);
    }

    public boolean matchesMajorVersionFormat(ComponentVersionFormat componentVersionFormat, String version) {
        return formatter.matchesFormat(componentVersionFormat.getMajorVersionFormat(), version);
    }

    public boolean matchesReleaseVersionFormat(ComponentVersionFormat componentVersionFormat, String version) {
        return formatter.matchesFormat(componentVersionFormat.getReleaseVersionFormat(), version);
    }

    public boolean matchesBuildVersionFormat(ComponentVersionFormat componentVersionFormat, String version) {
        return formatter.matchesFormat(componentVersionFormat.getBuildVersionFormat(), version);
    }

    public boolean matchesBuildVersionFormatNonStrict(ComponentVersionFormat componentVersionFormat, String version) {
        return formatter.matchesNonStrictFormat(componentVersionFormat.getBuildVersionFormat(), version);
    }

    public boolean matchesLineVersionFormat(ComponentVersionFormat componentVersionFormat, String version) {
        return formatter.matchesFormat(componentVersionFormat.getLineVersionFormat(), version);
    }

    public boolean matchesLineVersionFormatNonStrict(ComponentVersionFormat componentVersionFormat, String version) {
        return formatter.matchesNonStrictFormat(componentVersionFormat.getLineVersionFormat(), version);
    }

    public boolean matchesReleaseVersionFormatNonStrict(ComponentVersionFormat componentVersionFormat, String version) {
        return formatter.matchesNonStrictFormat(componentVersionFormat.getReleaseVersionFormat(), version);
    }

    public boolean matchesMajorVersionFormatNonStrict(ComponentVersionFormat componentVersionFormat, String version) {
        return formatter.matchesNonStrictFormat(componentVersionFormat.getMajorVersionFormat(), version);
    }

    public boolean matchesHotfixVersionFormat(ComponentVersionFormat componentVersionFormat, String version) {
        return formatter.matchesFormat(componentVersionFormat.getHotfixVersionFormat(), version);
    }

    public boolean matchesHotfixVersionFormatNonStrict(ComponentVersionFormat componentVersionFormat, String version) {
        return formatter.matchesNonStrictFormat(componentVersionFormat.getHotfixVersionFormat(), version);
    }

}
