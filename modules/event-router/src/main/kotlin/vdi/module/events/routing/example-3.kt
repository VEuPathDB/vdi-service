package vdi.module.events.routing

import com.fasterxml.jackson.module.kotlin.readValue
import com.rabbitmq.client.ConnectionFactory
import vdi.components.json.JSON
import vdi.module.events.routing.model.MinIOEvent

object Example3 {

  @JvmStatic
  fun main(args: Array<String>) {
    val conFac = ConnectionFactory().apply {
      username = "someUser"
      password = "somePassword"
      host = "localhost"
      port = 5672
    }

    conFac.newConnection().use { rabbitCon ->
      rabbitCon.createChannel().use { rabbitChan ->
        rabbitChan.exchangeDeclare("vdi-bucket-notifications", "direct", true)
        rabbitChan.queueDeclare("vdi-bucket-notifications", true, false, false, mapOf())
          .let { ok -> rabbitChan.queueBind(ok.queue, "vdi-bucket-notifications", "vdi-bucket-notifications") }

        rabbitChan.basicGet("vdi-bucket-notifications", false)
          ?.also {
//            println(it)
            println("messageCount = " + it.messageCount)
            println("envelope     = " + it.envelope)
            println("body         = " + JSON.readValue<MinIOEvent>(it.body))

            rabbitChan.basicAck(it.envelope.deliveryTag, false)
          }


      }
    }
  }

}