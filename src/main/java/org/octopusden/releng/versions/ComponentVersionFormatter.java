package org.octopusden.releng.versions;

public class ComponentVersionFormatter {

    public String formatMajor(IVersionInfo versionInfo,  ComponentVersionFormat componentVersionFormat) {
        return versionInfo.formatVersion(componentVersionFormat.getMajorVersionFormat());
    }

    public String formatRelease(IVersionInfo versionInfo,  ComponentVersionFormat componentVersionFormat) {
        return versionInfo.formatVersion(componentVersionFormat.getReleaseVersionFormat());
    }

    public String formatBuildVersion(IVersionInfo versionInfo,  ComponentVersionFormat componentVersionFormat) {
        return versionInfo.formatVersion(componentVersionFormat.getBuildVersionFormat());
    }

    public String formatLineVersion(IVersionInfo versionInfo, ComponentVersionFormat componentVersionFormat) {
        return versionInfo.formatVersion(componentVersionFormat.getLineVersionFormat());
    }

    public String formatHotFixVersion(IVersionInfo versionInfo, ComponentVersionFormat componentVersionFormat) {
        return versionInfo.formatVersion(componentVersionFormat.getHotfixVersionFormat());
    }
}
