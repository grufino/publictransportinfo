package data.io

import java.time.LocalTime

import data.Time
import data.config.Session
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.{Dataset, Row}
import org.apache.spark.storage.StorageLevel

object ReadTimes extends CsvDf[Time] {
  private var dataset: Dataset[Time] = null

  def get = dataset

  def update(affectedLinesDs: Dataset[Row]) = {
    dataset.unpersist()
    val spark = Session.get
    import spark.implicits._
    this.dataset =
      dataset
        .join(affectedLinesDs, Seq("lineId"), "left")
        .withColumn(
          "delayInSeconds",
          when($"newDelayInSeconds".isNotNull, $"newDelayInSeconds")
            .otherwise(col("delayInSeconds"))
        )
        .drop("line_id", "newDelayInSeconds", "line_name", "lineName")
        .as[Time]
        .persist(StorageLevel.MEMORY_ONLY)

  }

  def readNew() = {
      val spark = Session.get
      import spark.implicits._
      val stringToSecondInt = udf((time: String) => LocalTime.parse(time).toSecondOfDay)
      val path = "data/times.csv"
      this.dataset =
        spark.read
          .option("header", true)
          .csv(path)
          .withColumn("lineId", 'line_id.cast(IntegerType))
          .withColumn("stopId", 'stop_id.cast(IntegerType))
          .withColumn("timeInSeconds", stringToSecondInt(col("time")))
          .withColumn("delayInSeconds", lit(0))
          .drop("line_id", "stop_id")
          .as[Time]
          .persist(StorageLevel.MEMORY_ONLY)
  }
}
