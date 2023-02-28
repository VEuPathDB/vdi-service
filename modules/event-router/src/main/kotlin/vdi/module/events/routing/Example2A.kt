package vdi.module.events.routing

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import java.util.*
import java.util.concurrent.CountDownLatch

fun createTopics() {
  val client = AdminClient.create(Properties().apply {
    setProperty(AdminClientConfig.CLIENT_ID_CONFIG, "abaddon")
    setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  })

  val existingTopics = client.listTopics()
    .names()
    .get()

  val topicsToCreate = listOf(TOPIC)
    .stream()
    .filter { it !in existingTopics }
    .map { NewTopic(it, 1, 1) }
    .toList()

  if (topicsToCreate.isNotEmpty())
    client.createTopics(topicsToCreate)
}

object Example2A {
  @JvmStatic
  fun main(args: Array<String>) {
    createTopics()

    val producer = makeProducer()

    while (true) {
      producer.send(ProducerRecord(TOPIC, "timestamp", System.currentTimeMillis().toString()))
      Thread.sleep(1000)
    }
  }
}

object Example2B {
  @JvmStatic
  fun main(args: Array<String>) {
    val serde = Serdes.String()
    val cdl   = CountDownLatch(1)

    StreamsBuilder()
      .also {
        it.stream(TOPIC, Consumed.with(serde, serde))
          .foreach { k, v -> println("$k: $v") }
      }
      .build()
      .let { KafkaStreams(it, Properties().apply {
        setProperty(StreamsConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString())
        setProperty(StreamsConfig.APPLICATION_ID_CONFIG, UUID.randomUUID().toString())
        setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        setProperty(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString())
      }) }
      .apply { setStateListener { newState, oldState ->
        if (oldState == KafkaStreams.State.RUNNING && newState == KafkaStreams.State.NOT_RUNNING)
          cdl.countDown()
      } }
      .start()

    cdl.await()
  }
}