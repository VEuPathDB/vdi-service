package vdi.service.plugin.service

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.io.path.absolutePathString
import kotlin.io.path.createFile
import kotlin.io.path.outputStream
import kotlin.time.Duration.Companion.seconds
import vdi.json.JSON
import vdi.model.DatasetMetaFilename
import vdi.service.plugin.consts.ExitStatus
import vdi.service.plugin.metrics.ScriptMetrics
import vdi.service.plugin.script.PluginScript
import vdi.service.plugin.script.PluginScriptException
import vdi.service.plugin.script.ScriptExecutor
import vdi.service.plugin.server.context.InstallMetaContext
import vdi.util.io.LoggingOutputStream

class InstallMetaHandler(context: InstallMetaContext, executor: ScriptExecutor, metrics: ScriptMetrics)
: AbstractInstallHandler<Unit, InstallMetaContext>(context, executor, metrics, context.markedLogger()) {
  private inline val script
    get() = scriptContext.scriptConfig

  private inline val meta
    get() = scriptContext.metadata

  override suspend fun runJob() {
    val metaFile = workspace.resolve(DatasetMetaFilename)
      .createFile()
      .apply { outputStream().use { JSON.writeValue(it, meta) } }

    val timer = metrics.installMetaScriptDuration.startTimer()

    executor.executeScript(script.path, workspace, arrayOf(datasetID.toString(), metaFile.absolutePathString()), buildScriptEnv()) {
      coroutineScope {
        val logJob = launch { LoggingOutputStream(logger).use { scriptStdErr.transferTo(it) } }

        waitFor(script.maxDuration)

        logJob.join()

        val installMetaStatus = ExitStatus.InstallMeta.fromCode(exitCode())

        metrics.installMetaCalls.labelValues(installMetaStatus.metricFriendlyName).inc()

        when (installMetaStatus) {
          ExitStatus.InstallMeta.Success -> {
            val dur = timer.observeDuration()
            logger.info("script completed successfully in {}", dur.seconds)
          }

          else -> {
            val err = "script failed with exit code ${exitCode()}"
            logger.error(err)
            throw PluginScriptException(PluginScript.InstallMeta, err)
          }
        }
      }
    }
  }
}
