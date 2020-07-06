package data.delays

import data.config.Session
import data.{DelayTuple, Line}
import org.apache.spark.sql.{Dataset, Row}
import org.apache.spark.sql.functions._

object DelayProcessor {

  def processDelaysToLines(linesDs: Dataset[Line], delays: List[DelayTuple]): Dataset[Row] = {
    val spark = Session.get
    import spark.implicits._

    val withDelayUdf = udf((lineName: String) => {
      val delay = delays.find(tuple => tuple.lineName == lineName)
      delay match {
        case Some(value) => value.delayInSeconds
        case None        => 0
      }
    })

      linesDs
        .withColumn("newDelayInSeconds", withDelayUdf(col("lineName")))
        .filter($"newDelayInSeconds".notEqual(0))
  }

}
