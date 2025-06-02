package vdi.core.db.app.health

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
  extra    = extra.appendDbInfo(connection.details),
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

private fun Map<String, Any>.appendDbInfo(entry: TargetDatabaseDetails): Map<String, Any> {
  val map = if (this is MutableMap)
    this
  else
    this.toMutableMap()

  map["host"] = entry.server.host
  map["controlSchema"] = entry.schema

  return map
}
