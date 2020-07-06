package delays

import java.time.LocalTime

import data.config.Session
import data.delays.DelayProcessor
import data.finders.LineDelayFinder
import data.{DelayTuple, Line, Time}
import org.scalatest.FunSuite

class DelaysTest extends FunSuite {

  val date1 = LocalTime.parse("05:00:00").toSecondOfDay
  val date2 = LocalTime.parse("06:00:00").toSecondOfDay
  val date3 = LocalTime.parse("07:00:00").toSecondOfDay
  val date4 = LocalTime.parse("08:00:00").toSecondOfDay

  val linesData = Seq(Line(0, "U8"), Line(1, "M2"))

  val timesData = Seq(
    Time(0, 0, date1, 0),
    Time(0, 1, date2, 0),
    Time(0, 2, date3, 0),
    Time(1, 0, date1, 0),
    Time(1, 1, date3, 0),
    Time(1, 2, date4, 0)
  )

  test("delays are computed for lines") {
    val spark = Session.get
    import spark.implicits._

    val linesDs = linesData.toDF().as[Line]

    val newTimes =
      DelayProcessor
        .processDelaysToLines(linesDs, List(DelayTuple("U8", 1000)))
        .takeAsList(6)

    newTimes.get(0).get(2) == 1000
  }

  test("get line delay") {
    val spark = Session.get
    import spark.implicits._

    val linesDs = linesData.toDF().as[Line]
    val timesDs = timesData.toDF().as[Time]

    val delayProcessor =
      DelayProcessor
        .processDelaysToLines(linesDs, List(DelayTuple("U8", 1000)))
        .takeAsList(6)

    val delay = LineDelayFinder.forLine(linesDs, timesDs, "U8")

    assert(delay.delayInSeconds == 0)
  }
}
