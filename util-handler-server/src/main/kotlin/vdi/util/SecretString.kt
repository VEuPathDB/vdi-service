package vdi.util

@JvmInline
value class SecretString(val value: String) {
  override fun toString(): String {
    return "***"
  }
}