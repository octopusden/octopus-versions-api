package org.octopusden.buildsystem.its.version;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultBuildVersionPatternCalculatorTest {

  private DefaultBuildVersionPatternCalculator calculator = new DefaultBuildVersionPatternCalculator();

  @Test
  void testMavenFormat() {
    assertEquals("(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<service>\\d+)",  calculator.calculate("$major.$minor.$service"));
    assertEquals("(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<service>\\d+)-(?<fix>\\d+)",  calculator.calculate("$major.$minor.$service-$fix"));
    assertEquals("(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<service>\\d+)-(?<fix>\\d+)",  calculator.calculate("$major.$minor.$service-$fix04"));
  }

  @Test
  void testCardsFormat() {
    assertEquals("(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<service>\\d+)\\.(?<fix>\\d+)",  calculator.calculate("$major02.$minorCards.$serviceCards.$fix02"));
  }

}
