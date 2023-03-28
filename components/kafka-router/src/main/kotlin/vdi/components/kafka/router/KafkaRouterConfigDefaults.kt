package vdi.components.kafka.router

object KafkaRouterConfigDefaults {
  const val IMPORT_TRIGGER_MESSAGE_KEY = "import-trigger"
  const val IMPORT_TRIGGER_TOPIC = "import-triggers"

  const val INSTALL_TRIGGER_MESSAGE_KEY = "install-trigger"
  const val INSTALL_TRIGGER_TOPIC = "install-triggers"

  const val UPDATE_META_TRIGGER_MESSAGE_KEY = "update-meta-trigger"
  const val UPDATE_META_TRIGGER_TOPIC = "update-meta-triggers"

  const val SOFT_DELETE_TRIGGER_MESSAGE_KEY = "soft-delete-trigger"
  const val SOFT_DELETE_TRIGGER_TOPIC = "soft-delete-triggers"

  const val HARD_DELETE_TRIGGER_MESSAGE_KEY = "hard-delete-trigger"
  const val HARD_DELETE_TRIGGER_TOPIC = "hard-delete-triggers"

  const val SHARE_TRIGGER_MESSAGE_KEY = "share-trigger"
  const val SHARE_TRIGGER_TOPIC = "share-triggers"
}