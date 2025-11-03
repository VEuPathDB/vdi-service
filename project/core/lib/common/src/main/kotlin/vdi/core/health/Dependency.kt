package vdi.core.health

interface Dependency {
  val name: String
  val protocol: String
  val host: String
  val port: UShort
  val extra: Map<String, Any>

  enum class Status {
    Ok,
    NotOk,
    Unknown,
  }

  fun checkStatus(): Status

  fun urlString() =
    if (protocol.isNotBlank())
      "$protocol://$host"
    else
      host
}
