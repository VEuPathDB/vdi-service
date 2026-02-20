package vdi.core.db.app.health

import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import vdi.core.db.app.TargetDatabaseReference
import vdi.core.health.Dependency
import vdi.core.health.StaticDependency
import vdi.db.app.TargetDBPlatform
import vdi.db.app.TargetDatabaseDetails

class DatabaseDependency(
  private val connection: TargetDatabaseReference,
  extra: Map<String, Any> = emptyMap(),
): StaticDependency(
  name     = connection.identifier,
  protocol = "",
  host     = connection.details.server.host,
  port     = connection.details.server.port,
  extra    = extra.appendDbInfo(connection.details, connection.dataSource as HikariDataSource),
) {
  override fun checkStatus() =
    when (connection.details.platform) {
      TargetDBPlatform.Postgres -> pgStatus()
      TargetDBPlatform.Oracle   -> oraStatus()
    }

  private fun oraStatus() = runQuery("SELECT 1 FROM dual")

  private fun pgStatus() = runQuery("SELECT 1")

  private fun runQuery(query: String) =
    try {
      connection.dataSource.connection.use { c -> c.createStatement().use { s -> s.execute(query) } }
      Dependency.Status.Ok
    } catch (e: Throwable) {
      LoggerFactory.getLogger(javaClass).error("postgres database healthcheck failed", e)
      Dependency.Status.NotOk
    }
}

private fun Map<String, Any>.appendDbInfo(entry: TargetDatabaseDetails, ref: HikariDataSource): Map<String, Any> {
  val map = this as? MutableMap ?: this.toMutableMap()

  map["platform"] = entry.platform.name
  map["host"] = entry.server.host
  map["controlSchema"] = entry.schema
  map["activeConnections"] = ref.hikariPoolMXBean.activeConnections
  map["idleConnections"] = ref.hikariPoolMXBean.idleConnections

  return map
}
