package org.octopusden.releng.versions;

public class VersionRangeFactory {
    private final VersionNames versionNames;

    public VersionRangeFactory(VersionNames versionNames) {
        this.versionNames = versionNames;
    }

    public VersionRangeImpl create(String versionRange) {
        return new VersionRangeImpl(versionNames, versionRange);
    }
}
