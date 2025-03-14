package vdi.component.db.cache.health

import org.slf4j.LoggerFactory
import vdi.component.db.cache.CacheDBImpl
import vdi.health.Dependency
import vdi.health.StaticDependency

internal class DatabaseDependency(private val connection: CacheDBImpl)
  : StaticDependency("Internal Cache DB", "", connection.details.host, connection.details.port)
{
  override fun checkStatus() =
    try {
      connection.dataSource.connection.use { c -> c.createStatement().use { s -> s.execute("SELECT 1") } }
      Dependency.Status.Ok
    } catch (e: Throwable) {
      LoggerFactory.getLogger(javaClass).error("postgres database healthcheck failed", e)
      Dependency.Status.NotOk;
    }
}
