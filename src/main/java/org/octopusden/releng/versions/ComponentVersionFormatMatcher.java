package org.octopusden.releng.versions;

public final class ComponentVersionFormatMatcher {

    private final VersionNames versionNames;

    private final KotlinVersionFormatter formetter;


    public ComponentVersionFormatMatcher(VersionNames versionNames) {
        this.versionNames = versionNames;
        formetter = new KotlinVersionFormatter(versionNames);
    }

    public boolean matchesMajorVersionFormat(ComponentVersionFormat componentVersionFormat, String version) {
        return formetter.matchesFormat(componentVersionFormat.getMajorVersionFormat(), version);
    }

    public boolean matchesReleaseVersionFormat(ComponentVersionFormat componentVersionFormat, String version) {
        return formetter.matchesFormat(componentVersionFormat.getReleaseVersionFormat(), version);
    }

    public boolean matchesBuildVersionFormat(ComponentVersionFormat componentVersionFormat, String version) {
        return formetter.matchesFormat(componentVersionFormat.getBuildVersionFormat(), version);
    }

    public boolean matchesBuildVersionFormatNonStrict(ComponentVersionFormat componentVersionFormat, String version) {
        return formetter.matchesNonStrictFormat(componentVersionFormat.getBuildVersionFormat(), version);
    }

    public boolean matchesLineVersionFormat(ComponentVersionFormat componentVersionFormat, String version) {
        return formetter.matchesFormat(componentVersionFormat.getLineVersionFormat(), version);
    }

    public boolean matchesLineVersionFormatNonStrict(ComponentVersionFormat componentVersionFormat, String version) {
        return formetter.matchesNonStrictFormat(componentVersionFormat.getLineVersionFormat(), version);
    }

    public boolean matchesReleaseVersionFormatNonStrict(ComponentVersionFormat componentVersionFormat, String version) {
        return formetter.matchesNonStrictFormat(componentVersionFormat.getReleaseVersionFormat(), version);
    }

    public boolean matchesMajorVersionFormatNonStrict(ComponentVersionFormat componentVersionFormat, String version) {
        return formetter.matchesNonStrictFormat(componentVersionFormat.getMajorVersionFormat(), version);
    }


}
