package vdi.core.kafka.router

object RouterDefaults {
  @JvmStatic
  inline val ImportTriggerMessageKey
    get() = "import-trigger"
  @JvmStatic
  inline val ImportTriggerTopic
    get() = "import-triggers"

  @JvmStatic
  inline val InstallTriggerMessageKey
    get() = "install-trigger"
  @JvmStatic
  inline val InstallTriggerTopic
    get() = "install-triggers"

  @JvmStatic
  inline val UpdateMetaTriggerMessageKey
    get() = "update-meta-trigger"
  @JvmStatic
  inline val UpdateMetaTriggerTopic
    get() = "update-meta-triggers"

  @JvmStatic
  inline val SoftDeleteTriggerMessageKey
    get() = "soft-delete-trigger"
  @JvmStatic
  inline val SoftDeleteTriggerTopic
    get() = "soft-delete-triggers"

  @JvmStatic
  inline val HardDeleteTriggerMessageKey
    get() = "hard-delete-trigger"
  @JvmStatic
  inline val HardDeleteTriggerTopic
    get() = "hard-delete-triggers"

  @JvmStatic
  inline val ShareTriggerMessageKey
    get() = "share-trigger"
  @JvmStatic
  inline val ShareTriggerTopic
    get() = "share-triggers"

  @JvmStatic
  inline val ReconciliationTriggerMessageKey
    get() = "reconciliation-trigger"
  @JvmStatic
  inline val ReconciliationTriggerTopic
    get() = "reconciliation-triggers"
}
