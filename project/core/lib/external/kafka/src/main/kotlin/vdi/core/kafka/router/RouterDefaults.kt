package vdi.core.kafka.router

object RouterDefaults {
  inline val ImportTriggerMessageKey
    get() = "import-trigger"
  inline val ImportTriggerTopic
    get() = "import-triggers"

  inline val InstallTriggerMessageKey
    get() = "install-trigger"
  inline val InstallTriggerTopic
    get() = "install-triggers"

  inline val UpdateMetaTriggerMessageKey
    get() = "update-meta-trigger"
  inline val UpdateMetaTriggerTopic
    get() = "update-meta-triggers"

  inline val SoftDeleteTriggerMessageKey
    get() = "soft-delete-trigger"
  inline val SoftDeleteTriggerTopic
    get() = "soft-delete-triggers"

  inline val HardDeleteTriggerMessageKey
    get() = "hard-delete-trigger"
  inline val HardDeleteTriggerTopic
    get() = "hard-delete-triggers"

  inline val ShareTriggerMessageKey
    get() = "share-trigger"
  inline val ShareTriggerTopic
    get() = "share-triggers"

  inline val ReconciliationTriggerMessageKey
    get() = "reconciliation-trigger"
  inline val ReconciliationTriggerTopic
    get() = "reconciliation-triggers"
}
