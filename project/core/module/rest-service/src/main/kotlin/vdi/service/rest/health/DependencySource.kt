package vdi.service.rest.health

import org.veupathdb.lib.container.jaxrs.health.Dependency
import org.veupathdb.lib.container.jaxrs.health.Dependency.Status
import org.veupathdb.lib.container.jaxrs.health.Dependency.TestResult
import org.veupathdb.lib.container.jaxrs.health.DependencySource
import org.veupathdb.lib.container.jaxrs.health.ExternalDependency
import vdi.core.health.RemoteDependencies
import vdi.logging.logger
import vdi.core.health.Dependency as VDep

internal class DependencySource : DependencySource {
  override fun iterator(): MutableIterator<Dependency> {
    return object : MutableIterator<Dependency> {
      private val raw = RemoteDependencies.iterator()

      override fun hasNext() = raw.hasNext()

      override fun next(): Dependency = RemoteDependency(raw.next())

      override fun remove() {
        throw UnsupportedOperationException()
      }
    }
  }
}

private fun VDep.Status.toCoreStatus() = when (this) {
  VDep.Status.OK -> Status.ONLINE
  VDep.Status.NOT_OK -> Status.OFFLINE
  VDep.Status.UNKNOWN -> Status.UNKNOWN
}

private class RemoteDependency(private val raw: VDep) : ExternalDependency(raw.name) {
  override fun close() {}
  override fun test(): TestResult {
    logger().info("Checking health for external dependency {}", this.name)
    val status = raw.checkStatus().toCoreStatus()
    val reachable = (status == Status.UNKNOWN && pinger.isReachable(raw.host, raw.port))
      || status == Status.ONLINE
    return TestResult(this, reachable, status, raw.extraFields)
  }
}
