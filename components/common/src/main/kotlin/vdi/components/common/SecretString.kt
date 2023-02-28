package vdi.components.common

@JvmInline
value class SecretString(val value: String) {
  override fun toString() = "***"
}