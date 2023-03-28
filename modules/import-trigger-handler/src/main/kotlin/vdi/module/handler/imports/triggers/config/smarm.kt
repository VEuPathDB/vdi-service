package vdi.module.handler.imports.triggers.config

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.*
import vdi.components.common.ShutdownSignal
import vdi.components.common.jobs.WorkerPool
import vdi.components.common.util.HostAddress
import vdi.components.kafka.KafkaConsumer
import vdi.components.kafka.KafkaConsumerConfig

// TODO:
//  | Read messages from kafka topic and queue up jobs to print the contents as
//  | a proof of concept for the trigger/result handlers


fun main() {
  val con = KafkaConsumer(
    "flumps",
    KafkaConsumerConfig(
      servers = arrayOf(HostAddress("localhost", 9092u)),
      groupID = "taco",
      clientID = "bell",
      pollDuration = Duration.parse("1000ms")
    )
  )


  val workers = WorkerPool(5)

  runBlocking(newFixedThreadPoolContext(5, "smarm")) {
    workers.start(this)
    val trigger = ShutdownSignal()

    launch {
      delay(100.seconds)
      trigger.trigger()
    }


    while (!trigger.isTriggered()) {
      println("polling")
      con.receive().forEach {
        println("submitting $it")
        workers.submit { println(it.value) }
      }
    }

    println("shutting down")
    workers.stop()
  }

  con.close()
}

