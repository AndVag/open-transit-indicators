package com.azavea.opentransit.indicators

import com.azavea.gtfs._
import scala.collection.mutable
import com.azavea.opentransit.indicators.calculators._
import com.azavea.opentransit.indicators.parameters._

object Indicators {
  // These are indicators that don't need the request info.
  private val staticIndicators: List[Indicator] =
    List(
        AverageServiceFrequency,
        DistanceStops,
        Length,
        NumRoutes,
        NumStops,
        TimeTraveledStops,
        InterstopDistance,
        StopsToLength
    )

  // These are indicators that need to know things about the request
  private def paramIndicators(params: IndicatorParams): List[Indicator] = {
    case class Requires(requirements: Seq[Boolean]=Seq())
    val settings = params.settings

    List( // Tuples of requirements and params-requiring indicators
            (
                new CoverageRatioStopsBuffer(params),
                Requires(Seq(settings.hasCityBounds))
            ),
            (
                new TransitNetworkDensity(params),
                Requires(Seq(settings.hasRegionBounds))
            ),
            (
                new TravelTimePerformance(params),
                Requires(Seq(settings.hasObserved))
            ),
            (
                new DwellTimePerformance(params),
                Requires(Seq(settings.hasObserved))
            ),
            (
                new AllWeightedServiceFrequency(params),
                Requires(Seq(settings.hasDemographics))
            ),
            (
                new LowIncomeWeightedServiceFrequency(params),
                Requires(Seq(settings.hasDemographics))
            ),
            (
                new AllAccessibility(params),
                Requires(Seq(settings.hasDemographics))
            ),
            (
                new LowIncomeAccessibility(params),
                Requires(Seq(settings.hasDemographics))
            ),
            (
                new Affordability(params),
                Requires()
            )
    ).map { case (indicator: Indicator, reqs: Requires) =>
      if (reqs.requirements.foldLeft(true)(_ && _)) Some(indicator) else None
    }.flatten
  }


  def list(params: IndicatorParams): List[Indicator] =
    staticIndicators ++ paramIndicators(params)
}

trait Indicator { self: AggregatesBy =>
  val name: String
  def calculation(period: SamplePeriod): IndicatorCalculation

  def aggregatesBy(aggregate: Aggregate) =
    self.aggregates.contains(aggregate)

  def perTripCalculation[T](mapFunc: Trip => T, reduceFunc: Seq[T] => Double): IndicatorCalculation =
    new PerTripIndicatorCalculation[T] {
      def apply(system: TransitSystem): AggregatedResults =
        apply(system, Indicator.this.aggregatesBy _)

      def map(trip: Trip): T = mapFunc(trip)
      def reduce(results: Seq[T]): Double = reduceFunc(results)
    }

  def perRouteCalculation[T](mapFunc: Seq[Trip] => T, reduceFunc: Seq[T] => Double): IndicatorCalculation =
    new PerRouteIndicatorCalculation[T] {
      def apply(system: TransitSystem): AggregatedResults =
        apply(system, Indicator.this.aggregatesBy _)

      def map(trips: Seq[Trip]): T = mapFunc(trips)
      def reduce(results: Seq[T]): Double = reduceFunc(results)
    }

  def perSystemCalculation(calculate: TransitSystem => AggregatedResults) =
    new IndicatorCalculation {
      def apply(system: TransitSystem) = calculate(system)
    }
}
