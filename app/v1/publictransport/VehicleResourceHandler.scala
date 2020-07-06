package v1.publictransport


import data.{LineDelay, LineDelayJson, VehicleFindFailure, VehicleFindJson, VehicleForLocationInput, VehicleForStopInput}
import javax.inject.Inject
import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class VehicleResourceHandler @Inject()(publicTransportRepo: PublicTransportRepository)(implicit ec: ExecutionContext) {

  def getVehicleForLocation(inputX: String, inputY: String, inputTime: Option[String])(
    implicit mc: MarkerContext): Future[Either[VehicleFindFailure, VehicleFindJson]] = {
    val tryInput = createVehicleLocationInput(inputX, inputY, inputTime)
    tryInput match {
      case Success(input) => publicTransportRepo.getVehicleForLocation(input.x, input.y, input.time)
      case Failure(_) => Future {
        Left(VehicleFindFailure("No match for given Input Parameters"))
      }
    }
  }

  def getVehicleForStop(inputStop: String, inputTime: Option[String])(
    implicit mc: MarkerContext): Future[Either[VehicleFindFailure, VehicleFindJson]] = {
    val tryInput = createVehicleStopInput(inputStop, inputTime)
    tryInput match {
      case Success(input) => publicTransportRepo.getVehicleForStop(input.stop, input.time)
      case Failure(_) => Future {
        Left(VehicleFindFailure("No match for given Input Parameters"))
      }
    }
  }

  def getDelay(line: String)(
    implicit mc: MarkerContext): Future[LineDelayJson] = {
    for {
      lineDelay <- publicTransportRepo.getDelay(line)
    } yield createDelayOutput(lineDelay)
  }

  private def createDelayOutput(lineDelay: LineDelay): LineDelayJson = {
    val isDelayed = if(lineDelay.delayInSeconds > 0) true else false
    LineDelayJson(lineDelay.lineName, lineDelay.delayInSeconds, isDelayed)
  }

  private def createVehicleLocationInput(x: String, y: String, time: Option[String]): Try[VehicleForLocationInput] = {
    Try[VehicleForLocationInput]{VehicleForLocationInput(x.toInt, y.toInt, time: Option[String])}
  }

  private def createVehicleStopInput(stop: String, time: Option[String]): Try[VehicleForStopInput] = {
    Try[VehicleForStopInput]{VehicleForStopInput(stop.toInt, time: Option[String])}
  }

}
