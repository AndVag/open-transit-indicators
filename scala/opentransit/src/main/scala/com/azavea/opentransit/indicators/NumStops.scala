package com.azavea.opentransit.indicators

import com.azavea.gtfs._
import com.azavea.opentransit._
import com.azavea.opentransit.DjangoAdapter._
import scala.slick.jdbc.JdbcBackend.DatabaseDef

object NumStops extends Indicator
                   with AggregatesByAll {

  val name = "num_stops"

  val calc =
    new PerTripIndicatorCalculation {
      type Intermediate = Seq[Stop]

      def map(trip: Trip) =
        trip.schedule.map(_.stop)

      def reduce(stops: Seq[Seq[Stop]]) =
        stops.flatten.distinct.size
    }
}

// Number of stops
// class NumStops(val gtfsData: GtfsData, val calcParams: CalcParams, val db: DatabaseDef) extends IndicatorCalculator {
//   val name = "num_stops"

//   def calcByRoute(period: SamplePeriod): Map[String, Double] = {
//     println("in calcByRoute for NumStops")
//     // for each route, find the maximum number of stops across all trips
//     routesInPeriod(period).map(route =>
//       route.id.toString -> tripsInPeriod(period, route)
//         .foldLeft(0.0) {(max, trip) =>
//         val stops = stopsInPeriod(period, trip)
//         if (stops.isEmpty || stops.length < max) {
//           max
//         } else {
//           stops.length
//         }
//       }).toMap
//   }

//   def calcByMode(period: SamplePeriod): Map[Int, Double] = {
//     println("in calcByMode for NumStops")
//      // get all routes, group by route type, and find the unique stop ids per route (via trips)
//     routesInPeriod(period)
//       .groupBy(_.route_type.id)
//       .map { case (key, value) => 
//         key -> { 
//           value
//             .map(_.id)
//             .map { routeID => 
//               tripsInPeriod(period, routeByID(routeID))
//                 .map(stopsInPeriod(period, _).map(_.stop_id))
//                 .flatten
//               }
//             .flatten
//             .distinct
//             .length
//             .toDouble
//         }
//        }
//   }

//   def calcBySystem(period: SamplePeriod): Double = {
//     println("in calcBySystem for NumStops")
//     routesInPeriod(period)
//       .map(route =>
//         tripsInPeriod(period, routeByID(route.id.toString))
//           .flatMap(stopsInPeriod(period, _).map(_.stop_id))
//     ).flatten.distinct.length
//   }
// }
