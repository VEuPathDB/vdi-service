package vdi.module.handler.imports.triggers

import org.slf4j.LoggerFactory
import vdi.components.kafka.KafkaConsumer
import vdi.components.kafka.KafkaProducer
import vdi.module.handler.imports.triggers.config.ImportTriggerHandlerConfig

class ImportTriggerHandlerImpl(private val config: ImportTriggerHandlerConfig) : ImportTriggerHandler {

  private val log = LoggerFactory.getLogger(javaClass)

  @Volatile
  private var started = false

  override suspend fun start() {
    if (!started) {
      log.info("starting import-trigger-handler module")

      started = true
      run()
    }
  }

  override suspend fun stop() {
    TODO("this should use a ShutdownSignal to indicate that the process wants to stop, then block and wait for shutdown confirmation")
  }

  private suspend fun run() {
    // get a Kafka consumer for reading messages from the import trigger topic
    val kc = KafkaConsumer(config.kafkaConfig.importTriggerTopic, config.kafkaConfig.consumerConfig)

    // get a Kafka producer for writing messages to the import result topic
    val kp = KafkaProducer(config.kafkaConfig.producerConfig)

    // set up executor pool of some configurable number of executors
    // read messages from the kafka consumer
    // update the cache-db record for the target dataset to say it is importing
    // retrieve the dataset data from S3
    // call the plugin handler import endpoint with the dataset data
    // update the cache-db record for the target dataset with its import result status
    // write any import warnings or errors to the cache db
    // write the result (on success only) of the import to S3
    // post a message to the kafka producer with the result status
  }

}