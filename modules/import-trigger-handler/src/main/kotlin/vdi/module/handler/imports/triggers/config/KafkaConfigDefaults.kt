package vdi.module.handler.imports.triggers.config

@Deprecated("these values are defined in the kafka router config defaults as part of the vdi-component-kafka package")
object KafkaConfigDefaults {
  const val ImportTriggerMessageKey = "import-trigger"
  const val ImportTriggerTopic = "import-triggers"
}