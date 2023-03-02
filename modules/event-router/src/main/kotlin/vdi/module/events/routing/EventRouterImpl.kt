package vdi.module.events.routing

import org.slf4j.LoggerFactory
import vdi.components.common.ShutdownSignal
import vdi.module.events.routing.config.EventRouterConfig
import vdi.module.events.routing.rabbit.RabbitMQBucketEventSource

internal class EventRouterImpl(private val config: EventRouterConfig) : EventRouter {

  private val log = LoggerFactory.getLogger(javaClass)

  private val shutdownTrigger = ShutdownSignal()
  private val shutdownConfirm = ShutdownSignal()

  @Volatile
  private var started = false

  override suspend fun start() {
    log.info("starting event-router module")

    if (!started) {
      started = true
      run()
    }
  }

  override suspend fun stop() {
    log.info("triggering event-router shutdown")
    shutdownTrigger.trigger()
    shutdownConfirm.await()
  }

  private suspend fun run() {
    val es = RabbitMQBucketEventSource(config.rabbitConfig)
    val stream = es.stream(shutdownTrigger)

    while (!shutdownTrigger.isTriggered() && stream.hasNext()) {
      // This is just a temporary stand-in for testing.
      println(stream.next())
    }

    log.info("shutting down event-router")

    es.close()
    shutdownConfirm.trigger()
  }
}

