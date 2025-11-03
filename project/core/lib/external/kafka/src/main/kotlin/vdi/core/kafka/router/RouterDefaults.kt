package vdi.core.kafka.router

import vdi.core.kafka.MessageKey
import vdi.core.kafka.MessageTopic

object RouterDefaults {
  inline val ImportTriggerMessageKey
    get() = MessageKey("import-trigger")
  inline val ImportTriggerTopic
    get() = MessageTopic("import-triggers")

  inline val InstallTriggerMessageKey
    get() = MessageKey("install-trigger")
  inline val InstallTriggerTopic
    get() = MessageTopic("install-triggers")

  inline val UpdateMetaTriggerMessageKey
    get() = MessageKey("update-meta-trigger")
  inline val UpdateMetaTriggerTopic
    get() = MessageTopic("update-meta-triggers")

  inline val SoftDeleteTriggerMessageKey
    get() = MessageKey("soft-delete-trigger")
  inline val SoftDeleteTriggerTopic
    get() = MessageTopic("soft-delete-triggers")

  inline val HardDeleteTriggerMessageKey
    get() = MessageKey("hard-delete-trigger")
  inline val HardDeleteTriggerTopic
    get() = MessageTopic("hard-delete-triggers")

  inline val ShareTriggerMessageKey
    get() = MessageKey("share-trigger")
  inline val ShareTriggerTopic
    get() = MessageTopic("share-triggers")

  inline val ReconciliationTriggerMessageKey
    get() = MessageKey("reconciliation-trigger")
  inline val ReconciliationTriggerTopic
    get() = MessageTopic("reconciliation-triggers")
}
