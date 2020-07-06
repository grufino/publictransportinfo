package data.io

import data.{Line}
import data.config.Session
import org.apache.spark.sql.Dataset
import org.apache.spark.storage.StorageLevel

object ReadLines extends CsvDf[Line] {

  private var dataset: Dataset[Line] = null

  def get = this.dataset

  def readNew() = {
    val spark = Session.get
    import spark.implicits._
    val path = "data/lines.csv"

    this.dataset = spark.read
      .option("header", true)
      .csv(path)
      .withColumn("lineId", 'line_id.cast("integer"))
      .withColumn("lineName", 'line_name.cast("string"))
      .as[Line]
      .persist(StorageLevel.MEMORY_ONLY)
  }
}
