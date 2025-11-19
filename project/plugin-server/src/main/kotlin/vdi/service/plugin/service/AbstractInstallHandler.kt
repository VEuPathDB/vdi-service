package vdi.service.plugin.service

import org.slf4j.Logger
import vdi.db.app.TargetDatabaseDetails
import vdi.db.app.dbiName
import vdi.service.plugin.consts.ScriptEnvKey
import vdi.service.plugin.metrics.ScriptMetrics
import vdi.service.plugin.script.ScriptExecutor
import vdi.service.plugin.server.context.InstallScriptContext

abstract class AbstractInstallHandler<T, C: InstallScriptContext<*>>(
  scriptContext: C,
  executor: ScriptExecutor,
  metrics: ScriptMetrics,
  logger: Logger,
): AbstractScriptHandler<T, C>(scriptContext, executor, metrics, logger) {
  protected inline val projectID
    get() = scriptContext.installTarget

  protected inline val installPath
    get() = scriptContext.installPath

  protected inline val dbDetails
    get() = scriptContext.databaseConfig

  override fun appendScriptEnv(env: MutableMap<String, String>) {
    env[ScriptEnvKey.InstallPath] = installPath.toString()
    env[ScriptEnvKey.ProjectID]   = projectID
    env.putAll(dbDetails.toEnvMap())
  }

  private fun TargetDatabaseDetails.toEnvMap() = mapOf(
    ScriptEnvKey.DBHost to server.host,
    ScriptEnvKey.DBPort to server.port.toString(),
    ScriptEnvKey.DBName to name,
    ScriptEnvKey.DBUser to user,
    ScriptEnvKey.DBPass to pass.asString,
    ScriptEnvKey.DBSchema to schema,
    ScriptEnvKey.DBPlatform to platform.dbiName,
  )
}