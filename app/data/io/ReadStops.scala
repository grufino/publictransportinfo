package data.io

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import data.{Line, Stop}
import data.config.Session
import org.apache.spark.storage.StorageLevel
import play.api.{Configuration, Environment}
import javax.inject._
import org.apache.parquet.format.IntType
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.types.DataType
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

object ReadStops extends CsvDf[Stop] {

  private var dataset: Dataset[Stop] = null

  def get = this.dataset

  def readNew() = {
    val spark = Session.get
    import spark.implicits._

    val path = "data/stops.csv"

    this.dataset = spark
      .read
      .option("header", true)
      .csv(path)
      .withColumn("stopId", 'stop_id.cast("integer"))
      .withColumn("x", 'x.cast("integer"))
      .withColumn("y", 'y.cast("integer"))
      .as[Stop]
      .persist(StorageLevel.MEMORY_ONLY)
  }
}