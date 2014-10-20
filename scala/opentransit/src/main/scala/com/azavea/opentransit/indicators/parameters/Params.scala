package com.azavea.opentransit.indicators.parameters

import com.azavea.gtfs.{TransitSystem, Stop}

import com.azavea.opentransit._
import com.azavea.opentransit.database.{ BoundariesTable, RoadsTable, DemographicsTable }
import com.azavea.opentransit.indicators._

import geotrellis.slick._
import geotrellis.vector._

import scala.slick.jdbc.JdbcBackend.{Database, DatabaseDef, Session}


case class IndicatorSettings(
  povertyLine: Double,
  nearbyBufferDistance: Double,
  maxCommuteTime: Int,
  maxWalkTime: Int,
  averageFare: Double,
  hasDemographics: Boolean,
  hasOsm: Boolean,
  hasObserved: Boolean,
  hasCityBounds: Boolean,
  hasRegionBounds: Boolean
)

// Do not change by period or scenario
trait StaticParams {
  val settings: IndicatorSettings
}

trait IndicatorParams extends Boundaries
                         with StopBuffers
                         with RoadLength
                         with StaticParams
                         with Demographics
                         with ObservedStopTimes

object IndicatorParams {
  def apply(request: IndicatorCalculationRequest, systems: Map[SamplePeriod, TransitSystem], db: DatabaseDef): IndicatorParams =
    db withSession { implicit session =>
      val stopBuffers = StopBuffers(systems, request.nearbyBufferDistance, db)
      val demographics = Demographics(db)
      val observedStopTimes =
        ObservedStopTimes(systems, request.paramsRequirements.observed)

      new IndicatorParams {
        def observedForTrip(period: SamplePeriod, tripId: String) =
          observedStopTimes.observedForTrip(period, tripId)
        def observedStopsByTrip(period: SamplePeriod) =
          observedStopTimes.observedStopsByTrip(period)


        def bufferForStop(stop: Stop): Projected[MultiPolygon] = stopBuffers.bufferForStop(stop)
        def bufferForStops(stops: Seq[Stop]): Projected[MultiPolygon] = stopBuffers.bufferForStops(stops)
        def observedStopsByTrip(period: SamplePeriod) =
          observedStopTimes.observedStopsByTrip(period)

        def bufferForPeriod(period: SamplePeriod): Projected[MultiPolygon] =
          stopBuffers.bufferForPeriod(period)


        def populationMetricForBuffer(buffer: Projected[MultiPolygon], columnName: String) =
          demographics.populationMetricForBuffer(buffer, columnName)

        val settings =
          IndicatorSettings(
            request.povertyLine,
            request.nearbyBufferDistance,
            request.maxCommuteTime,
            request.maxWalkTime,
            request.averageFare,
            hasDemographics = request.paramsRequirements.demographics,
            hasOsm = request.paramsRequirements.osm,
            hasObserved = request.paramsRequirements.observed,
            hasCityBounds = request.paramsRequirements.cityBounds,
            hasRegionBounds = request.paramsRequirements.regionBounds
          )
        val cityBoundary = Boundaries.cityBoundary(request.cityBoundaryId)
        val regionBoundary = Boundaries.cityBoundary(request.regionBoundaryId)

        val totalRoadLength = RoadLength.totalRoadLength
      }
    }
}
