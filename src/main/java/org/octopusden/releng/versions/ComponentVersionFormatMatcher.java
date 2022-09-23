package org.octopusden.releng.versions;

public final class ComponentVersionFormatMatcher {

    private static final KotlinVersionFormatter FORMATTER = new KotlinVersionFormatter();

    public boolean matchesMajorVersionFormat(ComponentVersionFormat componentVersionFormat, String version) {
        return FORMATTER.matchesFormat(componentVersionFormat.getMajorVersionFormat(), version);
    }

    public boolean matchesReleaseVersionFormat(ComponentVersionFormat componentVersionFormat, String version) {
        return FORMATTER.matchesFormat(componentVersionFormat.getReleaseVersionFormat(), version);
    }

    public boolean matchesBuildVersionFormat(ComponentVersionFormat componentVersionFormat, String version) {
        return FORMATTER.matchesFormat(componentVersionFormat.getBuildVersionFormat(), version);
    }

    public boolean matchesBuildVersionFormatNonStrict(ComponentVersionFormat componentVersionFormat, String version) {
        return FORMATTER.matchesNonStrictFormat(componentVersionFormat.getBuildVersionFormat(), version);
    }

    public boolean matchesLineVersionFormat(ComponentVersionFormat componentVersionFormat, String version) {
        return FORMATTER.matchesFormat(componentVersionFormat.getLineVersionFormat(), version);
    }

    public boolean matchesLineVersionFormatNonStrict(ComponentVersionFormat componentVersionFormat, String version) {
        return FORMATTER.matchesNonStrictFormat(componentVersionFormat.getLineVersionFormat(), version);
    }

    public boolean matchesReleaseVersionFormatNonStrict(ComponentVersionFormat componentVersionFormat, String version) {
        return FORMATTER.matchesNonStrictFormat(componentVersionFormat.getReleaseVersionFormat(), version);
    }

    public boolean matchesMajorVersionFormatNonStrict(ComponentVersionFormat componentVersionFormat, String version) {
        return FORMATTER.matchesNonStrictFormat(componentVersionFormat.getMajorVersionFormat(), version);
    }


}
