package org.octopusden.releng.versions;

/**
 * Simple implementation maven based version range specification.
 * The original maven implementation is not used because of the problem with comparing
 * versions in different formats, e.g (2.0-1, 3.1-5) will not contain "2.0.2" version.
 */
public interface VersionRange {

    boolean containsVersion(final IVersionInfo version);
    boolean isIntersect(final VersionRange other);
}