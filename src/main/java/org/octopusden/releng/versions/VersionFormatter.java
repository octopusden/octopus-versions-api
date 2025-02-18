package org.octopusden.releng.versions;

public interface VersionFormatter {

    String format(String format, IVersionInfo version);

    String formatToCustomerVersion(String customerFormat, String versionFormat, String versionPrefix, IVersionInfo versionInfo);

    String formatToCustomerVersion(String customerFormat, String versionFormat, String versionPrefix, String hotfixSuffix, IVersionInfo versionInfo);

    boolean matchesFormat(String format, String version);

    boolean matchesFormat(String customerFormat, String versionFormat, String versionPrefix, String version);

    boolean matchesNonStrictFormat(String format, String version);
}
