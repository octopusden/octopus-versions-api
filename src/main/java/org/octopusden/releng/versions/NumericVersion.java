package org.octopusden.releng.versions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class NumericVersion implements IVersionInfo {

    private static final int FORTH_ITEM_INDEX = 3;
    private static final int FIFTH_ITEM_INDEX = 4;
    private final List<Integer> items;

    private final String rawVersion;
    private final boolean snapshot;
    private final boolean rcVersion;
    private final VersionNames versionNames;

    NumericVersion(VersionNames versionNames,
                           List<Integer> items,
                           String rawVersion,
                           boolean snapshot,
                           boolean rcVersion) {
        this.snapshot = snapshot;
        this.items = Collections.unmodifiableList(items);
        this.rawVersion = rawVersion;
        this.rcVersion = rcVersion;
        this.versionNames = versionNames;
    }

    @Override
    public String toString() {
        return rawVersion;
    }

    @Override
    public int getItem(int index) {
        return items.size() > index ? items.get(index) : 0;
    }

    @Override
    public int getItemsCount() {
        return items.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NumericVersion)) {
            return false;
        }
        return compareTo((NumericVersion) obj) == 0;
    }

    @Override
    public int hashCode() {
        return rawVersion.hashCode();
    }


    @Override
    public int getMajor() {
        return getItem(0);
    }

    @Override
    public int getMinor() {
        return getItem(1);
    }

    @Override
    public int getService() {
        return getItem(2);
    }

    @Override
    public int getFix() {
        return getItem(FORTH_ITEM_INDEX);
    }

    @Override
    public int getBuildNumber() {
        return getItem(FIFTH_ITEM_INDEX);
    }

    @Override
    public boolean isSnapshot() {
        return snapshot;
    }

    @Override
    public String formatVersion(String format) {
        return new KotlinVersionFormatter(versionNames).format(format, this);
    }

    public boolean isRcVersion() {
        return rcVersion;
    }

    @Override
    public int compareTo(IVersionInfo o) {
        int maxItemsCount = Math.max(items.size(), o.getItemsCount());
        for (int i = 0; i < maxItemsCount; i++) {
            if (getItem(i) != o.getItem(i)) {
                return getItem(i) > o.getItem(i) ? 1 : -1;
            }
        }
        return 0;
    }
}
