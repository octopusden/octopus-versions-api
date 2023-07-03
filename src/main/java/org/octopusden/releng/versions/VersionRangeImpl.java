package org.octopusden.releng.versions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class VersionRangeImpl implements VersionRange {
    private static final int MINIMUM_RANGE_LENGTH = 3;
    private final String versionRange;
    private final List<VersionRestriction> versionRestrictions = new ArrayList<>();

    VersionRangeImpl(VersionNames versionNames, String versionRange) {
        this.versionRange = versionRange;
        Arrays.stream(versionRange.split("(?<=[])])\\s*,\\s*(?=[\\[(])")).map( (range) -> VersionRestriction.parseInternal(range, versionNames)).forEach(versionRestrictions::add);
        for (int i = 0; i < versionRestrictions.size() - 1; i++) {
            if (versionRestrictions.get(i).right != null && versionRestrictions.get(i + 1).left != null &&
                    versionRestrictions.get(i).right.compareTo(versionRestrictions.get(i + 1).left) > 0) {
                throw new IllegalArgumentException("Bad range: the previous range " +
                        versionRestrictions.get(i).source + " overlaps next " + versionRestrictions.get(i + 1).source);
            }
        }
    }

    /**
     * Check if the specified version is in range.
     * @param version version to check
     * @return Returns true if the given version is in range, false otherwise
     */
    public boolean containsVersion(final IVersionInfo version) {
        return versionRestrictions.stream().anyMatch(versionRange -> versionRange.containsVersion(version));
    }

    /**
     * Check if the given version range intersects with this.
     * @param other version range to check for intersections with this
     * @return Returns true if the given version range intersects with this, false otherwise
     */
    public boolean isIntersect(final VersionRange other) {
        if (other.getClass() != getClass()) {
            throw new IllegalArgumentException("Cannot use " + other.getClass() + " as VersionRange");
        }
        VersionRangeImpl otherRange = (VersionRangeImpl) other;
        return versionRestrictions.stream().anyMatch(left -> {
            if (left.hardVersion) {
                return otherRange.versionRestrictions.stream().anyMatch(right -> right.containsVersion(left.left));
            }
            return otherRange.versionRestrictions.stream().anyMatch(right -> right.hardVersion && left.containsVersion(right.right) || !(
                    compareLeftLeftToRightLeft(left, right) < 0 && compareLeftRightToRightLeft(left, right) < 0 ||
                            compareLeftLeftToRightRight(left, right) > 0 && compareLeftRightToRightRight(left, right) > 0));
        });
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final VersionRangeImpl that = (VersionRangeImpl) o;
        return versionRange.equals(that.versionRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(versionRange);
    }

    @Override
    public String toString() {
        return "VersionRange='" + versionRange + '\'';
    }

    private static int restrictedCompare(final IVersionInfo left, final boolean leftIncluded,
                                         final IVersionInfo right, final boolean rightIncluded,
                                         int restrictedValue) {
        final int result = left.compareTo(right);
        if (result != 0 || leftIncluded && rightIncluded) {
            return result;
        }
        return restrictedValue;
    }

    private static int compareLeftLeftToRightLeft(final VersionRestriction left, final VersionRestriction right) {
        return left.left == null && right.left == null ? 0 : left.left == null ? -1 : right.left == null ? 1 :
                restrictedCompare(left.left, left.includeLeft, right.left, right.includeRight, -1);
    }

    private static int compareLeftLeftToRightRight(final VersionRestriction left, final VersionRestriction right) {
        return left.left == null && right.right == null ? 0 : left.left == null ? -1 : right.right == null ? 1 :
                restrictedCompare(left.left, left.includeLeft, right.right, right.includeRight, 1);
    }

    private static int compareLeftRightToRightLeft(final VersionRestriction left, final VersionRestriction right) {
        return left.right == null && right.left == null ? 0 : left.right == null || right.left == null ? 1 :
                restrictedCompare(left.right, left.includeRight, right.left, right.includeLeft, -1);
    }

    private static int compareLeftRightToRightRight(final VersionRestriction left, final VersionRestriction right) {
        return left.right == null && right.right == null ? 0 : left.right == null ? 1 : right.right == null ? -1 :
                restrictedCompare(left.right, left.includeRight, right.right, right.includeRight, -1);
    }

    private static final class VersionRestriction {
        private final String source;
        private final IVersionInfo left;
        private final boolean includeLeft;
        private final IVersionInfo right;
        private final boolean includeRight;
        private final boolean hardVersion;

        private VersionRestriction(String source, IVersionInfo left, boolean includeLeft, IVersionInfo right, boolean includeRight) {
            if (left == null && right == null) {
                throw new IllegalArgumentException("Bad range: no minimum, maximum allowed versions are specified " + source);
            }
            this.source = source;
            this.left = left;
            this.includeLeft = includeLeft;
            this.right = right;
            this.includeRight = includeRight;
            hardVersion = left != null &&  right != null && left.compareTo(right) == 0;
        }

        private boolean containsVersion(final IVersionInfo version) {
            Objects.requireNonNull(version, "Version can't be null");
            if (left != null && (includeLeft && left.compareTo(version) > 0 || !includeLeft && (left.compareTo(version) >= 0))) {
                return false;
            }
            if (right != null && (includeRight && right.compareTo(version) < 0 || !includeRight && (right.compareTo(version) <= 0))) {
                return false;
            }
            return true;
        }

        private static VersionRestriction parseInternal(final String versionRange, VersionNames versionNames) {
            final String[] versionRangeParts = versionRange.trim().split("\\s*,\\s*");
            if (versionRangeParts[0].length() == 0) {
                throw new IllegalArgumentException("Bad version range: there is no 'minimum' specification " + versionRange);
            }
            NumericVersionFactory numericVersionFactory = new NumericVersionFactory(versionNames);
            if (versionRangeParts.length == 1) {
                if (versionRangeParts[0].charAt(0) != '[') {
                    throw new IllegalArgumentException("Bad version range: the '[' doesn't specify hard version: " + versionRange);
                }
                if (versionRangeParts[0].length() < MINIMUM_RANGE_LENGTH || versionRangeParts[0].charAt(versionRangeParts[0].length() - 1) != ']') {
                    throw new IllegalArgumentException("Bad version range: the ']' doesn't specify hard version: " + versionRange);
                }
                final IVersionInfo version = numericVersionFactory.create(versionRangeParts[0].substring(1, versionRangeParts[0].length() - 1));
                return new VersionRestriction(versionRange, version, true, version, true);
            }

            if (versionRangeParts.length > 2) {
                throw new IllegalArgumentException("Bad version range: too many versions " + versionRange);
            }

            if (versionRangeParts[1].length() == 0) {
                throw new IllegalArgumentException("Bad version range: there is no 'maximum' specification " + versionRange);
            }
            final boolean includeLeft;
            final boolean includeRight;
            final String leftVersion;
            final String rightVersion;
            switch (versionRangeParts[0].charAt(0)) {
                case '[':
                    if (versionRangeParts[0].length() == 1) {
                        throw new IllegalArgumentException("Bad version range: the '[' doesn't specify start of range: " + versionRange);
                    }
                    includeLeft = true;
                    leftVersion = versionRangeParts[0].substring(1);
                    break;
                case '(':
                    includeLeft = false;
                    leftVersion = versionRangeParts[0].length() == 1 ? null : versionRangeParts[0].substring(1);
                    break;
                default:
                    throw new IllegalArgumentException("Bad version range: the 'soft' requirement isn't supported: " + versionRange);
            }

            switch (versionRangeParts[1].charAt(versionRangeParts[1].length() - 1)) {
                case ']':
                    if (versionRangeParts[1].length() == 1) {
                        throw new IllegalArgumentException("Bad version range: the ']' doesn't specify end of range: " + versionRange);
                    }
                    includeRight = true;
                    rightVersion = versionRangeParts[1].substring(0, versionRangeParts[1].length() - 1);
                    break;
                case ')':
                    includeRight = false;
                    rightVersion = versionRangeParts[1].length() == 1 ? null : versionRangeParts[1].substring(0, versionRangeParts[1].length() - 1);
                    break;
                default:
                    throw new IllegalArgumentException("Bad version range: the 'soft' requirement isn't supported: " + versionRange);
            }
            final IVersionInfo minimum = leftVersion != null ? numericVersionFactory.create(leftVersion) : null;
            final IVersionInfo maximum = rightVersion != null ? numericVersionFactory.create(rightVersion) : null;
            if (minimum != null && maximum != null) {
                if (minimum.compareTo(maximum) == 0) {
                    if (!includeLeft) {
                        throw new IllegalArgumentException("Bad version range: the '[' doesn't start range: " + versionRange);
                    }
                    if (!includeRight) {
                        throw new IllegalArgumentException("Bad version range: the ']' doesn't start range: " + versionRange);
                    }
                }
                if (minimum.compareTo(maximum) > 0) {
                    throw new IllegalArgumentException("Bad version range: the left (minimal) is greater than right (maximum): " + versionRange);
                }
            }
            return new VersionRestriction(versionRange, minimum, includeLeft, maximum, includeRight);
        }

        @Override
        public String toString() {
            return "VersionRange{" +
                    "source='" + source + '\'' +
                    ", left=" + left +
                    ", includeLeft=" + includeLeft +
                    ", right=" + right +
                    ", includeRight=" + includeRight +
                    '}';
        }
    }
}
