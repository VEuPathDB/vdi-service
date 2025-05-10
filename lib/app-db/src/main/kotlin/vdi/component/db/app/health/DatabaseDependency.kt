package vdi.component.db.app.health

import org.slf4j.LoggerFactory
import vdi.component.db.app.AppDBPlatform
import vdi.component.db.app.AppDBRegistryEntry
import vdi.health.Dependency
import vdi.health.StaticDependency

class DatabaseDependency(
  private val connection: AppDBRegistryEntry,
  extra: Map<String, Any> = emptyMap(),
) : StaticDependency(connection.name, "", connection.host, connection.port, extra.appendDbInfo(connection)) {
  override fun checkStatus() =
    when (connection.platform) {
      AppDBPlatform.Postgres -> pgStatus()
      AppDBPlatform.Oracle -> oraStatus()
    }

  private fun oraStatus() = runQuery("SELECT 1 FROM dual")

  private fun pgStatus() = runQuery("SELECT 1")

  private fun runQuery(query: String) =
    try {
      connection.source.connection.use { c -> c.createStatement().use { s -> s.execute(query) } }
      Dependency.Status.Ok
    } catch (e: Throwable) {
      LoggerFactory.getLogger(javaClass).error("postgres database healthcheck failed", e)
      Dependency.Status.NotOk;
    }
}

private fun Map<String, Any>.appendDbInfo(entry: AppDBRegistryEntry): Map<String, Any> {
  val map = if (this is MutableMap)
    this
  else
    this.toMutableMap()

  map["host"] = entry.host
  map["controlSchema"] = entry.ctlSchema
  map["dataSchema"] = entry.dataSchema

  return map
}
