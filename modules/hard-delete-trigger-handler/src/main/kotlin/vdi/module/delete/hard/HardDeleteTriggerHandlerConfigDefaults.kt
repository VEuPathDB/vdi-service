package vdi.module.delete.hard

import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfigDefaults

object HardDeleteTriggerHandlerConfigDefaults {
  const val JobQueueSize   = 10u
  const val WorkerPoolSize = 10u

  inline val HardDeleteTopic
    get() = KafkaRouterConfigDefaults.HARD_DELETE_TRIGGER_TOPIC

  inline val HardDeleteMesesageKey
    get() = KafkaRouterConfigDefaults.HARD_DELETE_TRIGGER_MESSAGE_KEY
}