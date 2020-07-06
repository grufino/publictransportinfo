package v1.publictransport

import java.time.LocalTime
import java.time.format.DateTimeFormatter

import javax.inject.{Inject, Singleton}
import akka.actor.ActorSystem
import data.finders.{LineDelayFinder, VehicleForLocationFinder}
import data.{LineDelay, VehicleFindData, VehicleFindFailure, VehicleFindJson}
import data.io.{ReadLines, ReadStops, ReadTimes}
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future
import scala.util.{Success, Try}

class PublicTransportExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait PublicTransportRepository {
  def getVehicleForLocation(x: Int, y: Int, time: Option[String])(implicit mc: MarkerContext): Future[Either[VehicleFindFailure, VehicleFindJson]]
  def getVehicleForStop(stopId: Int, time: Option[String])(implicit mc: MarkerContext): Future[Either[VehicleFindFailure, VehicleFindJson]]
  def getDelay(line: String)(implicit mc: MarkerContext): Future[LineDelay]
}

@Singleton
class PublicTransportRepositoryImpl @Inject()(implicit ec: PublicTransportExecutionContext)
    extends PublicTransportRepository {

  private val logger = Logger(this.getClass)

  override def getVehicleForLocation(x: Int, y: Int, time: Option[String])(
      implicit mc: MarkerContext): Future[Either[VehicleFindFailure, VehicleFindJson]] = {
    val timeTry = time match {
      case Some(v) => Try[Int]{LocalTime.parse(v).toSecondOfDay}
      case None => Try[Int]{LocalTime.now().toSecondOfDay}
    }

    timeTry match {
      case Success(time: Int) => Future {
        logger.trace(s"get: x = $x, y = $y, time = $time")
        VehicleForLocationFinder.forTimeAndPlace(ReadLines.get, ReadTimes.get, ReadStops.get, x, y, time) match {
          case Right(value) => Right(createOutput(value))
          case Left(value) => Left(value)
        }
      }
      case _ => Future{Left(VehicleFindFailure(s"Input time not parseable: ${time.get}"))}
    }
  }

  override def getVehicleForStop(stop: Int, time: Option[String])(
    implicit mc: MarkerContext): Future[Either[VehicleFindFailure, VehicleFindJson]] = {
    val timeTry = time match {
      case Some(v) => Try[Int]{LocalTime.parse(v).toSecondOfDay}
      case None => Try[Int]{LocalTime.now().toSecondOfDay}
    }

    timeTry match {
      case Success(time: Int) => Future {
        logger.trace(s"get stop $stop, time = $time")
        VehicleForLocationFinder.forTimeAndStop(ReadLines.get, ReadTimes.get, ReadStops.get, stop, time) match {
          case Right(value) => Right(createOutput(value))
          case Left(value) => Left(value)
        }
      }
      case _ => Future{Left(VehicleFindFailure(s"Input time not parseable: ${time.get}"))}
    }
  }

  override def getDelay(line: String)(
    implicit mc: MarkerContext): Future[LineDelay] = {
    Future(
      LineDelayFinder.forLine(ReadLines.get, ReadTimes.get, line)
    )
  }

  private def createOutput(vehicleFindData: VehicleFindData): VehicleFindJson = {
    val timeStr = LocalTime.ofSecondOfDay(vehicleFindData.timeInSeconds.toLong).toString
    VehicleFindJson(vehicleFindData.lineName, vehicleFindData.stopId, timeStr)
  }
}
