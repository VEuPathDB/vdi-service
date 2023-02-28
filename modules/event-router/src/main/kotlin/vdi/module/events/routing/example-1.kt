package vdi.module.events.routing

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.time.Duration
import java.util.Properties
import java.util.UUID

const val TOPIC = "vdi-bucket-notifications"


fun makeProducer() =
  Properties()
    .apply {
      put(ProducerConfig.CLIENT_ID_CONFIG, "mammon")
      put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    }
    .let { KafkaProducer(it, StringSerializer(), StringSerializer()) }

fun makeConsumer() =
  Properties()
    .apply {
      put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString())
      put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString())
      put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    }
    .let { KafkaConsumer(it, StringDeserializer(), StringDeserializer()) }

fun main() {
  if (System.currentTimeMillis() % 2 == 1L) {

    println("I'm a producer!")

    val producer = makeProducer()
    producer.send(ProducerRecord(TOPIC, "how", "it's just a graphical glitch")).get()

  } else {

    println("I'm a consumer!")

    val consumer = makeConsumer()
    consumer.subscribe(listOf(TOPIC))

    consumer.poll(Duration.ofSeconds(10))
      .forEach { println(it) }

  }
}