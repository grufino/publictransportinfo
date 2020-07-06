package data.finders

import data.config.Session
import data._
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions.{abs, col, udf}
import org.apache.spark.sql.functions.lit

import scala.util.{Failure, Success, Try}

object VehicleForLocationFinder {
  private val spark = Session.get

  def forTimeAndStop(linesDs: Dataset[Line], timesDs: Dataset[Time], stopsDs: Dataset[Stop], stop: Int, timeTarget: Int): Either[VehicleFindFailure, VehicleFindData] = {
    import spark.implicits._

    val stopData = stopsDs.filter($"stopId".equalTo(lit(stop))).first()

    forTimeAndPlace(linesDs, timesDs, stopsDs, stopData.x, stopData.y, timeTarget: Int)
  }

  def forTimeAndPlace(linesDs: Dataset[Line], timesDs: Dataset[Time], stopsDs: Dataset[Stop], xTarget: Int, yTarget: Int, timeTarget: Int): Either[VehicleFindFailure, VehicleFindData] = {

    import spark.implicits._

    val timesWithDifference =
      timesDs
        .withColumn("timeDifference", col("timeInSeconds") + col("delayInSeconds") - timeTarget)

    val bestLine = Try[VehicleFindDataWithDelay] {
      stopsDs
        .join(timesWithDifference, "stopId")
        .filter(s"x == ${lit(xTarget)} and y == ${lit(yTarget)} and timeDifference >= 0")
        .orderBy("timeDifference")
        .join(linesDs, "lineId")
        .as[VehicleFindDataWithDelay]
        .first()
    }

    bestLine match {
      case Success(vehicleFindDataWithDelay) => {
        Right(
          VehicleFindData(
            vehicleFindDataWithDelay.lineName,
            vehicleFindDataWithDelay.stopId,
            vehicleFindDataWithDelay.timeInSeconds + vehicleFindDataWithDelay.delayInSeconds
          )
        )
      }
      case Failure(_) => Left(VehicleFindFailure("No match for given Input Parameters"))
    }
  }
}
