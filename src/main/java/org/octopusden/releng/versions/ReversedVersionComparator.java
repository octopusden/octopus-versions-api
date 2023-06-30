package org.octopusden.releng.versions;


import java.io.Serializable;
import java.util.Comparator;

public class ReversedVersionComparator implements Comparator<String>, Serializable {

    private final VersionNames versionNames;

    public ReversedVersionComparator(VersionNames versionNames) {
        this.versionNames = versionNames;
    }

    /**
     * Compares its two version as NumericVersiob for order
     *
     * @param version1 version 1
     * @param version2 version 2
     * @return negative if #version1 is greater as NumericVersion than #version2
     */
    @Override
    public int compare(String version1, String version2) {
        NumericVersionFactory factory = new NumericVersionFactory(versionNames);
        IVersionInfo numericVersion1 = factory.create(version1);
        IVersionInfo numericVersion2 = factory.create(version2);
        return numericVersion2.compareTo(numericVersion1);
    }
}