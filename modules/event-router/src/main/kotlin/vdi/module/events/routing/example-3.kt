package vdi.module.events.routing

import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.*
import vdi.components.common.SecretString
import vdi.module.events.routing.config.*


// Dummy class for testing purposes.
object Example3 {

  private val config = EventRouterConfig(
    EventRouterRabbitConfig(
      serverUsername = "someUser",
      serverPassword = SecretString("somePassword"),
      exchangeName = "vdi-bucket-notifications",
      queueName = "vdi-bucket-notifications",
      routingKey = "vdi-bucket-notifications",
    )
  )

  @JvmStatic
  fun main(args: Array<String>) {
    val eventRouter = EventRouter(config)

    runBlocking {
      launch {
        println("starting")
        eventRouter.start()
      }

      launch {
        println("waiting")
        delay(30.seconds)
        println("shutting down")
        eventRouter.stop()
      }
    }
  }
}