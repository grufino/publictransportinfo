package v1.publictransport

import data.{VehicleFindFailure, VehicleFindJson}
import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext}

class PublicTransportController @Inject()(cc: PublicTransportControllerComponents)(
    implicit ec: ExecutionContext)
    extends PublicTransportBaseController(cc) {

  private val logger = Logger(getClass)

  def vehicleForLocation(x: String, y: String): Action[AnyContent] = PublicTransportAction.async {
    implicit request =>
      val time = request.getQueryString("time")
      logger.trace(s"get: x = ${x}, y = ${y}, time = ${time}")
      publicTransportResourceHandler.getVehicleForLocation(x, y, time).map { result =>
        result match {
          case Left(failure: VehicleFindFailure) => BadRequest(Json.toJson(failure))
          case Right(value: VehicleFindJson) => Ok(Json.toJson(Json.toJson(value)))
        }
      }
  }

  def lineDelay(line: String): Action[AnyContent] = PublicTransportAction.async {
    implicit request =>
      publicTransportResourceHandler.getDelay(line).map { delay =>
        Ok(Json.toJson(delay))
      }
  }

  def vehicleForStop(stop: String): Action[AnyContent] = PublicTransportAction.async {
    implicit request =>
      val time = request.getQueryString("time")
      publicTransportResourceHandler.getVehicleForStop(stop, time).map { result =>
        result match {
          case Left(failure: VehicleFindFailure) => BadRequest(Json.toJson(failure))
          case Right(value: VehicleFindJson) => Ok(Json.toJson(value))
        }
      }
  }
}
