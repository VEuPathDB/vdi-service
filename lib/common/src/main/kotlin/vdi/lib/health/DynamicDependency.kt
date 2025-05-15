package vdi.lib.health

open class DynamicDependency(
  override val name: String,
  override val protocol: String,
  override val host: String,
  override val port: UShort,
  val extraSupplier: () -> Map<String, Any>
) : Dependency {
  override val extra: Map<String, Any>
    get() = extraSupplier()

  override fun checkStatus() = Dependency.Status.Ok

  override fun toString() =
    if (protocol.isNotBlank())
      "$protocol://$host:$port"
    else
      "$host:$port"
}
