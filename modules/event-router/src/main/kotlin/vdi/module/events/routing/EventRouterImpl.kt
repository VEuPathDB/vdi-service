package vdi.module.events.routing

import vdi.components.common.ShutdownSignal
import vdi.module.events.routing.config.EventRouterConfig
import vdi.module.events.routing.rabbit.RabbitMQBucketEventSource

internal class EventRouterImpl(private val config: EventRouterConfig) : EventRouter {

  private val shutdownSignal = ShutdownSignal()

  @Volatile
  private var started = false

  override suspend fun start() {
    if (!started) {
      started = true
      run()
    }
  }

  override suspend fun stop() {
    shutdownSignal.trigger()
  }

  private suspend fun run() {
    val es = RabbitMQBucketEventSource(config.rabbitConfig)
    val stream = es.stream(shutdownSignal)

    while (!shutdownSignal.isTriggered() && stream.hasNext()) {
      println(stream.next())
    }

    es.close()
  }
}

