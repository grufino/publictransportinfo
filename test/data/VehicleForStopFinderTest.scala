package data

import java.time.LocalTime

import data.config.Session
import data.finders.VehicleForLocationFinder
import org.scalatest.FunSuite

class VehicleForStopFinderTest extends FunSuite {

  val date1 = LocalTime.parse("05:00:00").toSecondOfDay
  val date2 = LocalTime.parse("06:00:00").toSecondOfDay
  val date3 = LocalTime.parse("07:00:00").toSecondOfDay
  val date4 = LocalTime.parse("08:00:00").toSecondOfDay

  val stopsData = Seq(Stop(0, 1, 1), Stop(1, 2, 2), Stop(2, 3, 3))

  val linesData = Seq(Line(0, "U8"), Line(1, "M2"))

  val timesData = Seq(
    Time(0, 0, date1, 0),
    Time(0, 1, date2, 0),
    Time(0, 2, date3, 0),
    Time(1, 0, date1, 0),
    Time(1, 1, date3, 0),
    Time(1, 2, date4, 0)
  )

  test("best vehicle for a given time and X & Y coordinates") {
    val spark = Session.get
    import spark.implicits._

    val stopsDs = stopsData.toDF().as[Stop]
    val linesDs = linesData.toDF().as[Line]
    val timesDs = timesData.toDF().as[Time]

    assert(
      Right(VehicleFindData("U8", 2, 25200)) == VehicleForLocationFinder
        .forTimeAndPlace(linesDs, timesDs, stopsDs, 3, 3, date3)
    )
  }

  test("best vehicle for a given time and X & Y coordinates (time past)") {
    val spark = Session.get
    import spark.implicits._

    val stopsDs = stopsData.toDF().as[Stop]
    val linesDs = linesData.toDF().as[Line]
    val timesDs = timesData.toDF().as[Time]

    val dateLater = LocalTime.parse("07:01:00").toSecondOfDay

    assert(
      VehicleForLocationFinder
        .forTimeAndPlace(linesDs, timesDs, stopsDs, 3, 3, dateLater) == Right(
        VehicleFindData("M2", 2, 28800)
      )
    )
  }

  test(
    "no line found (missing x,y coordinate) vehicle for a given time and X & Y coordinates"
  ) {
    val spark = Session.get
    import spark.implicits._

    val stopsDs = stopsData.toDF().as[Stop]
    val linesDs = linesData.toDF().as[Line]
    val timesDs = timesData.toDF().as[Time]

    assert(
      Left(VehicleFindFailure("No match for given Input Parameters")) == VehicleForLocationFinder
        .forTimeAndPlace(linesDs, timesDs, stopsDs, 999, 999, date3)
    )
  }

  test(
    "line found for time close to existing vehicle for a given time and X & Y coordinates"
  ) {
    val spark = Session.get
    import spark.implicits._

    val stopsDs = stopsData.toDF().as[Stop]
    val linesDs = linesData.toDF().as[Line]
    val timesDs = timesData.toDF().as[Time]

    val unexistingDate = LocalTime.parse("07:00:01").toSecondOfDay

    assert(
      Right(VehicleFindData("M2", 2, 28800)) == VehicleForLocationFinder
        .forTimeAndPlace(linesDs, timesDs, stopsDs, 3, 3, unexistingDate)
    )
  }

  test("delays are considered when calculating best vehicle") {
    val spark = Session.get
    import spark.implicits._

    val stopsDs = stopsData.toDF().as[Stop]
    val linesDs = linesData.toDF().as[Line]

    val dateLaterButBeforeDelayed = LocalTime.parse("07:01:00").toSecondOfDay

    val timesDataWithDelay = Seq(
      Time(0, 0, date1, 0),
      Time(0, 1, date2, 0),
      Time(0, 2, date3, 1000),
      Time(1, 0, date1, 0),
      Time(1, 1, date3, 0),
      Time(1, 2, dateLaterButBeforeDelayed, 0)
    )

    val timesDs = timesDataWithDelay.toDF().as[Time]

    assert(
      Right(VehicleFindData("M2", 2, 25260)) == VehicleForLocationFinder
        .forTimeAndPlace(linesDs, timesDs, stopsDs, 3, 3, date3)
    )
  }

  test("delays appear in results") {
    val spark = Session.get
    import spark.implicits._

    val stopsDs = stopsData.toDF().as[Stop]
    val linesDs = linesData.toDF().as[Line]

    val dateLaterButStillAfterDelayed =
      LocalTime.parse("07:01:00").toSecondOfDay

    val timesDataWithDelay = Seq(
      Time(0, 0, date1, 0),
      Time(0, 1, date2, 0),
      Time(0, 2, date3, 5),
      Time(1, 0, date1, 0),
      Time(1, 1, date3, 0),
      Time(1, 2, dateLaterButStillAfterDelayed, 0)
    )

    val timesDs = timesDataWithDelay.toDF().as[Time]

    assert(
      Right(VehicleFindData("U8", 2, 25205)) == VehicleForLocationFinder
        .forTimeAndPlace(linesDs, timesDs, stopsDs, 3, 3, date3)
    )
  }

}
