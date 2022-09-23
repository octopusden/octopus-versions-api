package org.octopusden.releng.versions;

public interface IVersionInfo extends Comparable<IVersionInfo> {

    int getItem(int index);

    int getItemsCount();

    int getMajor();

    int getMinor();

    int getService();

    int getFix();

    int getBuildNumber();

    boolean isSnapshot();

    String formatVersion(String format);
}
