package vdi.service.rest.health

import org.veupathdb.lib.container.jaxrs.health.Dependency
import org.veupathdb.lib.container.jaxrs.health.Dependency.Status
import org.veupathdb.lib.container.jaxrs.health.Dependency.TestResult
import org.veupathdb.lib.container.jaxrs.health.DependencySource
import org.veupathdb.lib.container.jaxrs.health.ExternalDependency
import vdi.lib.health.RemoteDependencies
import vdi.lib.logging.logger
import vdi.lib.health.Dependency as VDep

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
  VDep.Status.Ok -> Status.ONLINE
  VDep.Status.NotOk -> Status.OFFLINE
  VDep.Status.Unknown -> Status.UNKNOWN
}

private class RemoteDependency(private val raw: VDep) : ExternalDependency(raw.name) {
  override fun close() {}
  override fun test(): TestResult {
    logger.info("Checking health for external dependency {}", this.name)
    return if (pinger.isReachable(raw.urlString(), raw.port.toInt()))
      TestResult(this, true, raw.checkStatus().toCoreStatus(), raw.extra)
    else
      TestResult(this, false, Status.UNKNOWN, raw.extra)
  }
}
