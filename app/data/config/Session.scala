package data.config

import org.apache.spark.sql.SparkSession

object Session {

  private var session: Option[SparkSession] = None

  def get: SparkSession = session match {
    case Some(value) => value
    case None => {
      val session = getNew()
      this.session = Some(session)
      session
    }
  }

    def getNew() = {
      SparkSession.builder().master("local").appName("publictransportinfo").getOrCreate()
    }
}
