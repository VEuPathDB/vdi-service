package vdi.module.handler.install.data.config

import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfigDefaults

object InstallTriggerHandlerConfigDefaults {
  const val WorkerPoolSize = 5u

  const val JobQueueSize   = 5u

  inline val InstallDataTriggerTopic
    get() = KafkaRouterConfigDefaults.INSTALL_TRIGGER_TOPIC

  inline val InstallDataTriggerMessageKey
    get() = KafkaRouterConfigDefaults.INSTALL_TRIGGER_MESSAGE_KEY
}