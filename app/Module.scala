import akka.stream.scaladsl.BroadcastHub
import com.google.inject.AbstractModule

import data.config.Session
import data.delays.DelaysConsumer
import data.io.{ReadLines, ReadStops, ReadTimes}
import javax.inject._
import net.codingwell.scalaguice.ScalaModule
import play.api.Logger
import v1.publictransport._

class Module extends AbstractModule with ScalaModule {

  private val logger = Logger(this.getClass)

  override def configure() = {
      logger.warn("Reading Stops")
      ReadStops.readNew()
      logger.warn("Reading Lines")
      ReadLines.readNew()
      logger.warn("Reading Times")
      ReadTimes.readNew()
      logger.warn("Starting Kafka Delays Consumer")
      bind(classOf[DelaysConsumer]).asEagerSingleton()
      bind[PublicTransportRepository].to[PublicTransportRepositoryImpl].in[Singleton]
  }
}
