package data.delays


import java.time.Duration
import java.util
import java.util.Properties

import akka.actor.ActorSystem
import data.DelayTuple
import data.io.{ReadLines, ReadStops, ReadTimes}
import javax.inject.{Inject, Singleton}

import scala.collection.JavaConverters._
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.common.serialization.{IntegerDeserializer, StringDeserializer}
import play.api.Logger

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class DelaysConsumer @Inject()(implicit ac: ActorSystem) {

  private val start = consumeFromKafka("delays")

  private def consumeFromKafka(topic: String) = {
    val delaysExecutionContext: ExecutionContext = ac.dispatchers.lookup("delays.consumer")
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.deserializer", classOf[StringDeserializer])
    props.put("value.deserializer", classOf[IntegerDeserializer])
    props.put("enable.auto.commit", "true")
    props.put("auto.offset.reset", "latest")
    props.put("group.id", "consumer-group")
    val consumer: KafkaConsumer[String, Integer] = new KafkaConsumer[String, Integer](props)
    consumer.subscribe(util.Arrays.asList(topic))
    val logger = Logger(getClass)
    Future {
      while (true) {
        val recordIt = consumer.poll(Duration.ofSeconds(10)).iterator()
        var recordsToUpdate = ListBuffer[DelayTuple]()
        while (recordIt.hasNext) {
          val rec: ConsumerRecord[String, Integer] = recordIt.next()
          val lineName = rec.key()
          val delayInSeconds = rec.value() * 60
          logger.warn(s"line ${lineName} will be ${delayInSeconds} seconds delayed")
          val tuple = DelayTuple(lineName, delayInSeconds)
          recordsToUpdate += tuple
        }
        val recordsToUpdateList = recordsToUpdate.toList
        if(recordsToUpdateList.size > 0){
          val timesWithNewDelays = DelayProcessor.processDelaysToLines(ReadLines.get, recordsToUpdateList)
          ReadTimes.update(timesWithNewDelays)
        }
      }
    }(delaysExecutionContext)
  }
}
