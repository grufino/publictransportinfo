package data.io

import org.apache.spark.sql.Dataset

trait CsvDf[T] {
  def get: Dataset[T]

  def readNew: Unit
}
