package com.azavea.opentransit.indicators

import com.azavea.gtfs._

import com.azavea.opentransit.testkit._
import com.azavea.opentransit.indicators.calculators._

import com.github.nscala_time.time.Imports._
import com.typesafe.config.{ConfigFactory,Config}

import org.scalatest._
import org.scalatest.OptionValues._

class TravelTimePerformanceSpec
    extends FlatSpec
    with Matchers
    with IndicatorSpec
    with ObservedStopTimeSpec {


  val observedMapping = new ObservedStopTimeSpecParams {}

  // Noise with a mean value of 7.5 minutes was introduced to create this simulated observational data
  it should "calculate travel time performance for SEPTA" in {
    val calculation = new TravelTimePerformance(observedMapping).calculation(period)
    val AggregatedResults(byRoute, byRouteType, bySystem) = calculation(system)
    bySystem.get should be (0.125 +- 1e-2)
  }

}
