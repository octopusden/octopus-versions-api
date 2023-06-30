package org.octopusden.releng.versions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NumericVersionFactory {

    final VersionNames versionNames;

    public NumericVersionFactory(VersionNames versionNames) {
        this.versionNames = versionNames;
    }

    public IVersionInfo create(String rawVersion) {
        Objects.requireNonNull(rawVersion, "version can't be null");
        final List<String> strings = new ArrayList<>();
        int l = 0;
        int r;
        for (r = 0; r < rawVersion.length(); r++) {
            char c = rawVersion.charAt(r);
            if (c == '_' || c == '.' || c == '-') {
                strings.add(rawVersion.substring(l, r));
                l = r + 1;
            }
        }
        strings.add(rawVersion.substring(l, r));

        ArrayList<Integer> items = new ArrayList<>();
        for (String stringItem : strings) {
            int item;
            try {
                item = Integer.parseInt(stringItem);
            } catch (NumberFormatException e) {
                continue;
//                item = 0;
            }
            items.add(item);
        }
        return new NumericVersion(versionNames, items, rawVersion, rawVersion.endsWith("-SNAPSHOT"), rawVersion.endsWith("_RC"));
    }

    public IVersionInfo create(int... elements) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int element : elements) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(".");
            }
            stringBuilder.append(element);
        }
        return create(stringBuilder.toString());
    }
}
