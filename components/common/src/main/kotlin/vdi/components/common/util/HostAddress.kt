package vdi.components.common.util

data class HostAddress(
  val host: String,
  val port: UShort
) {
  override fun toString() = "$host:$port"
}
