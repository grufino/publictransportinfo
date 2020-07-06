package data.finders

import data.config.Session
import data.{Line, LineDelay, Time}
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions.lit

object LineDelayFinder {

  def forLine(linesDs: Dataset[Line],
              timesDs: Dataset[Time],
              lineName: String): LineDelay = {
    val spark = Session.get
    import spark.implicits._

      linesDs
      .filter($"lineName".equalTo(lit(lineName)))
      .join(timesDs, "lineId")
      .as[LineDelay]
      .first()
  }
}
