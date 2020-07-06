import data.{LineDelayJson, VehicleFindJson}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

class PublicTransportRouterSpec extends PlaySpec with GuiceOneAppPerTest {

  "PublicTransportRouter" should {

    "render best vehicle when there is a line for that location" in {
      val request = FakeRequest(
        GET,
        "/v1/publicTransport/vehicleForLocation/1/1?time=05:00:00"
      ).withHeaders(HOST -> "localhost:8081").withCSRFToken
      val home: Future[Result] = route(app, request).get

      val vehicleMatch: VehicleFindJson =
        Json.fromJson[VehicleFindJson](contentAsJson(home)).get

      vehicleMatch mustBe VehicleFindJson("M4", 0, "05:00")
    }

    "render best vehicle for a given stop" in {
      val request = FakeRequest(
        GET,
        "/v1/publicTransport/vehicleForStop/0?time=05:00:00"
      ).withHeaders(HOST -> "localhost:8081").withCSRFToken
      val home: Future[Result] = route(app, request).get

      val vehicleMatch: VehicleFindJson =
        Json.fromJson[VehicleFindJson](contentAsJson(home)).get

      vehicleMatch mustBe VehicleFindJson("M4", 0, "05:00")
    }

    "render delay for stop" in {
      val request = FakeRequest(GET, "/v1/publicTransport/delay/M4")
        .withHeaders(HOST -> "localhost:8081")
        .withCSRFToken
      val home: Future[Result] = route(app, request).get

      val vehicleMatch: LineDelayJson =
        Json.fromJson[LineDelayJson](contentAsJson(home)).get

      vehicleMatch mustBe LineDelayJson("M4", 0, false)
    }
  }

}
