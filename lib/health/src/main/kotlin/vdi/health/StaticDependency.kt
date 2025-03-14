package vdi.health

open class StaticDependency(
  override val name: String,
  override val protocol: String,
  override val host: String,
  override val port: UShort,
  override val extra: Map<String, Any> = emptyMap()
) : Dependency {
  override fun toString() =
    if (protocol.isNotBlank())
      "$protocol://$host:$port"
    else
      "$host:$port"

  override fun checkStatus() = Dependency.Status.Ok
}
