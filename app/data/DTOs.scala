package data

import play.api.libs.json.{Json}

final case class Stop(stopId: Int, x: Int, y: Int)

final case class Line(lineId: Int, lineName: String)

final case class Time(lineId: Int, stopId: Int, timeInSeconds: Int, delayInSeconds: Int)

final case class VehicleFindDataWithDelay(lineName: String, stopId: Int, timeInSeconds: Int, delayInSeconds: Int)

final case class VehicleFindData(lineName: String, stopId: Int, timeInSeconds: Int)
final case class VehicleFindFailure(errorMessage: String)

final case class VehicleFindJson(lineName: String, stopId: Int, time: String)

final case class DelayTuple(lineName: String, delayInSeconds: Int)

final case class LineDelay(lineName: String, timeInSeconds: Int, delayInSeconds: Int)

final case class LineDelayJson(lineName: String, delayInSeconds: Int, isDelayed: Boolean)

object LineDelayJson {
  implicit val formatLineDelay = Json.format[LineDelayJson]
}

final case class VehicleForLocationInput(x: Int, y: Int, time: Option[String])

final case class VehicleForStopInput(stop: Int, time: Option[String])

object VehicleFindJson {
  implicit val formatVehicleFindJson = Json.format[VehicleFindJson]
}

object VehicleFindFailure {
  implicit val formatVehicleFindFailure = Json.format[VehicleFindFailure]
}