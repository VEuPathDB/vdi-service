package vdi.bootstrap

import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vdi.components.common.fields.SecretString
import vdi.components.common.VDIServiceModule
import vdi.components.datasets.paths.S3Paths
import vdi.components.rabbit.RabbitMQConfig
import vdi.module.events.routing.EventRouter
import vdi.module.events.routing.config.EventRouterConfig


object Main {

  private val log = LoggerFactory.getLogger(javaClass)

  // FIXME: This is for testing only!!!
  private val eventRouterConfig = EventRouterConfig(
    RabbitMQConfig(
      serverUsername = "someUser",
      serverPassword = SecretString("somePassword"),
      exchangeName = "vdi-bucket-notifications",
      queueName = "vdi-bucket-notifications",
      routingKey = "vdi-bucket-notifications",
    ),
    "some-other-bucket"
  )

  @JvmStatic
  fun main(args: Array<String>) {
    log.info("initializing modules")
    val modules = listOf<VDIServiceModule>(
      EventRouter(eventRouterConfig)
    )

    Runtime.getRuntime().addShutdownHook(Thread {
      log.info("shutting down modules")
      runBlocking {
        for (module in modules)
          launch { module.stop() }
      }
    })

    log.info("starting modules")
    runBlocking {
      for (module in modules)
        launch { module.start() }
    }
  }
}

object Flumps {
  @JvmStatic
  fun main(args: Array<String>) {
    val s3 = S3Api.newClient(S3Config(
      url       = "localhost",
      port      = 9000u,
      secure    = false,
      accessKey = "someToken",
      secretKey = "someSecretKey"
    ))

    val bucket = s3.buckets[BucketName("some-other-bucket")]!!

    bucket.objects.put(S3Paths.datasetMetaFile("123456", "146eff4f2050bf87dd8a14fd359a210d"), "contents".byteInputStream())
  }
}