package org.octopusden.buildsystem.its.version;

class DefaultBuildVersionPatternCalculator {

  String calculate(String fullMajorFormat) {
    return fullMajorFormat
            .replaceAll("\\$major02", "(?<major>\\\\d+)")
            .replaceAll("\\$major", "(?<major>\\\\d+)")
            .replaceAll("\\$minor02", "(?<minor>\\\\d+)")
            .replaceAll("\\$minorCards", "(?<minor>\\\\d+)")
            .replaceAll("\\$minor", "(?<minor>\\\\d+)")
            .replaceAll("\\$service02", "(?<service>\\\\d+)")
            .replaceAll("\\$serviceCards", "(?<service>\\\\d+)")
            .replaceAll("\\$service", "(?<service>\\\\d+)")
            .replaceAll("\\$fix02", "(?<fix>\\\\d+)")
            .replaceAll("\\$fix04", "(?<fix>\\\\d+)")
            .replaceAll("\\$fix", "(?<fix>\\\\d+)")
            .replaceAll("\\$build02", "(?<build>\\\\d+)")
            .replaceAll("\\$build04", "(?<build>\\\\d+)")
            .replaceAll("\\$build", "(?<build>\\\\d+)")
            .replaceAll("\\.", "\\\\.");
  }


}
