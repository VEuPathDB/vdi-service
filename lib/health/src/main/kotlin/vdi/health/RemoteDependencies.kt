package vdi.health

import io.klibs.collections.Deque

object RemoteDependencies : Iterable<Dependency> {
  private val registry = Deque<Dependency>()

  fun register(dependency: Dependency) {
    registry.append(dependency)
  }

  fun register(
    name: String,
    host: String,
    port: UShort,
    protocol: String = "",
    extraProvider: (() -> Map<String, Any>)? = null,
  ) {
    if (extraProvider != null)
      registry.append(DynamicDependency(name, protocol, host, port, extraProvider))
    else
      registry.append(StaticDependency(name, protocol, host, port))
  }

  override fun iterator(): Iterator<Dependency> {
    return object : Iterator<Dependency> {
      private val raw = registry.iterator()
      override fun hasNext() = raw.hasNext()
      override fun next() = raw.next()
    }
  }
}
