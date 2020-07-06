package v1.publictransport

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._


class PublicTransportRouter @Inject()(controller: PublicTransportController) extends SimpleRouter {
  val prefix = "/v1/publicTransport"

  override def routes: Routes = {
    case GET(p"/vehicleForLocation/$x/$y") =>
      controller.vehicleForLocation(x, y)

    case GET(p"/vehicleForStop/${stop}") =>
      controller.vehicleForStop(stop)

    case GET(p"/delay/${line}") =>
      controller.lineDelay(line)
  }

}
