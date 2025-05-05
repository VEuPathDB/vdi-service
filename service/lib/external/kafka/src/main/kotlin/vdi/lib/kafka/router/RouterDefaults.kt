package vdi.lib.kafka.router

object RouterDefaults {
  const val ImportTriggerMessageKey = "import-trigger"
  const val ImportTriggerTopic = "import-triggers"

  const val InstallTriggerMessageKey = "install-trigger"
  const val InstallTriggerTopic = "install-triggers"

  const val UpdateMetaTriggerMessageKey = "update-meta-trigger"
  const val UpdateMetaTriggerTopic = "update-meta-triggers"

  const val SoftDeleteTriggerMessageKey = "soft-delete-trigger"
  const val SoftDeleteTriggerTopic = "soft-delete-triggers"

  const val HardDeleteTriggerMessageKey = "hard-delete-trigger"
  const val HardDeleteTriggerTopic = "hard-delete-triggers"

  const val ShareTriggerMessageKey = "share-trigger"
  const val ShareTriggerTopic = "share-triggers"

  const val ReconciliationTriggerMessageKey = "reconciliation-trigger"
  const val ReconciliationTriggerTopic = "reconciliation-triggers"
}
