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
            Integer item = parseNonNegativeInt(stringItem);
            if (item != null) {
                items.add(item);
            }
        }
        return new NumericVersion(versionNames, items, rawVersion, rawVersion.endsWith("-SNAPSHOT"), rawVersion.endsWith("_RC"));
    }

    /**
     * Parses a version segment as a non-negative {@code int} without using exceptions for control flow.
     * Returns {@code null} for empty, non-numeric, or out-of-range segments (the common case for labelled
     * versions such as {@code 1.2.3-rc1} or {@code 1.2.3.Final}), so the hot path does not throw and catch
     * a {@link NumberFormatException} per segment on every call.
     */
    private static Integer parseNonNegativeInt(String segment) {
        int length = segment.length();
        // Integer.parseInt accepts a single leading '+', so preserve that (a lone '+' is not a number).
        int start = (length > 0 && segment.charAt(0) == '+') ? 1 : 0;
        if (start == length) {
            return null;
        }
        long value = 0;
        for (int i = start; i < length; i++) {
            char c = segment.charAt(i);
            if (c < '0' || c > '9') {
                return null;
            }
            value = value * 10 + (c - '0');
            if (value > Integer.MAX_VALUE) {
                return null;
            }
        }
        return (int) value;
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
