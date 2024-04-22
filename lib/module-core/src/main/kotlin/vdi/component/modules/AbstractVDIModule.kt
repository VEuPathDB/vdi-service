package vdi.component.modules

import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.S3Client
import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.json.JSON
import vdi.component.async.ShutdownSignal
import vdi.component.kafka.EventMessage
import vdi.component.kafka.KafkaConsumer
import vdi.component.kafka.KafkaConsumerConfig
import vdi.component.kafka.router.KafkaRouterConfig
import vdi.component.kafka.router.KafkaRouterFactory
import vdi.component.s3.DatasetManager

/**
 * VDI Service Module Abstract Base
 *
 * Provides common functionality used by most implementations of the
 * [VDIModule] interface.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
abstract class AbstractVDIModule(override val name: String, protected val abortCB: (String?) -> Nothing) : VDIModule {
  private val log = LoggerFactory.getLogger(javaClass)

  protected val shutdownTrigger = ShutdownSignal()

  protected val shutdownConfirm = ShutdownSignal()

  @Volatile
  private var started = false

  final override suspend fun start() {
    if (!started) {
      log.info("starting module {}", name)

      started = true
      try {
        run()
      } catch (e: Throwable) {
        log.error("module $name execution failed", e)
        abortCB(e.message)
      }
    }
  }

  final override suspend fun stop() {
    log.info("shutting down module {}", name)

    shutdownTrigger.trigger()
    shutdownConfirm.await()

    log.info("module {} shutdown confirmed", name)
  }

  /**
   * Indicates whether a module shutdown has been requested.
   */
  protected suspend fun isShutDown(): Boolean = shutdownTrigger.isTriggered()

  /**
   * Request the module shut down.
   */
  protected suspend fun triggerShutdown() = shutdownTrigger.trigger()

  /**
   * Confirm that the module is now shut down.
   */
  protected suspend fun confirmShutdown() = shutdownConfirm.trigger()

  protected abstract suspend fun run()

  /**
   * Executes the given function in a try block that will shut down the module
   * if an exception occurs.
   *
   * @param error Error message for the exception that will be thrown.
   *
   * @param fn Function to execute safely.
   *
   * @param T Return type of the given function.
   *
   * @return A value of type T as returned by the given function on successful
   * execution.
   */
  protected suspend fun <T> safeExec(error: String, fn: () -> T): T =
    try {
      fn()
    } catch (e: Throwable) {
      log.error("safeExec failed with error: $error", e)
      triggerShutdown()
      throw Exception(error, e)
    }

  /**
   * Requires a [KafkaConsumer] instance be successfully created and returned.
   *
   * If the `KafkaConsumer` could not be created, this method will shut down the
   * module with an exception.
   *
   * @param topic Topic the `KafkaConsumer` will listen to.
   *
   * @param config Kafka consumer configuration.
   *
   * @return A new `KafkaConsumer` instance.
   */
  protected suspend fun requireKafkaConsumer(topic: String, config: KafkaConsumerConfig) =
    safeExec("failed to create Kafka consumer client") { KafkaConsumer(topic, config) }

  /**
   * Requires an [S3Client] instance be successfully created and returned.
   *
   * If the `S3Client` could not be created, this method will shut down the
   * module with an exception.
   *
   * @param config S3 client configuration.
   *
   * @return A new `S3Client` instance.
   */
  protected suspend fun requireS3Client(config: S3Config) =
    safeExec("failed to create S3 client") { S3Api.newClient(config) }

  /**
   * Requires an [S3Bucket] instance be successfully retrieved and returned.
   *
   * If the `S3Bucket` instance could not be fetched from the given [S3Client],
   * this method will shut down the module with an exception.
   *
   * @param s3Client An [S3Client] instance that will be used to look up the
   * target bucket.
   *
   * @param bucketName Name of the target bucket to require.
   *
   * @return An `S3Bucket` instance.
   */
  protected suspend fun requireS3Bucket(s3Client: S3Client, bucketName: BucketName) =
    safeExec("failed to lookup S3 bucket") {
      s3Client.buckets[bucketName]
        ?: throw IllegalStateException("S3 bucket $bucketName does not exist!")
    }

  protected suspend fun requireDatasetManager(s3Config: S3Config, bucketName: BucketName) =
    DatasetManager(requireS3Bucket(requireS3Client(s3Config), bucketName))

  /**
   * Fetches messages from Kafka matching the given [key] and converts them into
   * a stream of values of type [T].
   *
   * Messages with a key that does not match the given [key] parameter will be
   * filtered out with a warning log message.
   *
   * Messages with a value that cannot be converted into type [T] will be
   * filtered out with an error log message.
   *
   * @param key Message key that incoming Kafka messages must match.
   *
   * @param type Class used to convert the incoming values into type [T].
   *
   * @return A stream of values of type [T]
   */
  protected fun KafkaConsumer.fetchMessages(key: String): Sequence<EventMessage> =
    receive()
      .asSequence()
      .filter {
        if (it.key == key) {
          true
        } else {
          log.warn("filtering out message with key {} as it does not match expected key {}", it.key, key)
          false
        }
      }
      .map {
        try {
          JSON.readValue(it.value, vdi.component.kafka.EventMessage::class.java)
        } catch (e: Throwable) {
          log.error("received invalid message body from Kafka: {}", it.value)
          null
        }
      }
      .filterNotNull()

  protected suspend fun requireKafkaRouter(config: KafkaRouterConfig) =
    safeExec("failed to create KafkaRouter instance") { KafkaRouterFactory(config).newKafkaRouter() }
}