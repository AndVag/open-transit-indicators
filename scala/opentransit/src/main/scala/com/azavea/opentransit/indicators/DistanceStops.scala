package com.azavea.opentransit.indicators

import com.azavea.gtfs._
import com.azavea.opentransit._
import com.azavea.opentransit.DjangoAdapter._
import scala.slick.jdbc.JdbcBackend.DatabaseDef

/** Indicator for average distance between stops */
object DistnaceStops extends Indicator
                        with AggregatesByAll {
  val name = "distance_stops"
  val calc =
    new PerRouteIndicatorCalculation {
      type Intermediate = Double

     // for each route, get tuple of:
     // (sum of trip shape lengths, maximum number of stops in any trip)
      def map(trips: Seq[Trip]) = {
        val (total, maxStops, count) = 
          trips
            .map { trip =>
              (trip.schedule.size, trip.tripShape.map(_.line.length))
             }
            .foldLeft((0.0, 0, 0)) { case ((total, maxStops, count), (stopCount, length)) =>
              length match {
                case Some(l) => (total + l, math.max(maxStops, stopCount), count + 1)
                case None => (total, maxStops, count)
              }
            }

        ((total / 1000) / count) / (maxStops - 1)
      }

      def reduce(routeAverages: Seq[Double]) = {
        val (total, count) =
          routeAverages
            .foldLeft((0.0, 0)) { case ((total, count), value) =>
              (total + value, count + 1)
             }
        total / count
      }
    }
}

// Average distance between stops
// class DistanceStops(val gtfsData: GtfsData, val calcParams: CalcParams, val db: DatabaseDef) extends IndicatorCalculator {
//   val name = "distance_stops"

//   def calcByRoute(period: SamplePeriod): Map[String, Double] =
//     // for each route, get tuple of:
//     // (sum of trip shape lengths, maximum number of stops in any trip)
//     routesInPeriod(period)
//       .map { route =>
//         val (totalLength, maxStops, count) = getDistancesByRoute(period, route)

//         // per route, calculate:
//         // average length of trips in km / max # of legs in any trip (total stops, -1)
//         val result =
//           ((totalLength / 1000) / count) / (maxStops - 1)

//         (route.id.toString, result)
//       }
//      .toMap


//   def calcByMode(period: SamplePeriod): Map[Int, Double] = {
//     println("in calcByMode for DistanceStops")
//     // get the average distance between stops per route, group by route type,
//     // and average all the distances
//     calcByRoute(period).toList
//       .groupBy(kv => routeByID(kv._1).route_type.id)
//       .map { case (key, value) => key -> value.map(_._2).sum / value.size }
//   }

//   def calcBySystem(period: SamplePeriod): Double = {
//     println("in calcBySystem for DistanceStops")
//     val (distance, numDistances) = routesInPeriod(period)
//       .map { route => getDistancesByRoute(period, route) }
//       .foldLeft((0.0, 0.0)) { case ((totalKilometers, numTrips), (tripMeters, maxStops, count)) =>
//         (totalKilometers + tripMeters / (maxStops - 1) / 1000, numTrips + count)
//       }

//     distance / numDistances
//   }

//   // Helper for getting the components of the distance between stops calculation, by route
//   def getDistancesByRoute(period: SamplePeriod, route: Route): (Double, Double, Double) = {
//     tripsInPeriod(period, route).foldLeft((0.0, 0.0, 0.0)) { (acc, trip) =>
//       val (totalLength, maxStops, count) = acc
//       trip.rec.shape_id match {
//         case None => (totalLength, maxStops, count + 1)
//         case Some(shapeID) => {
//           gtfsData.shapesById.get(shapeID) match {
//             case None => (totalLength, maxStops, count + 1)
//             case Some(tripShape) =>
//               (totalLength + tripShape.line.length,
//                 math.max(maxStops, trip.stops.size),
//                 count + 1)
//           }
//         }
//       }
//     }
//   }
// }
