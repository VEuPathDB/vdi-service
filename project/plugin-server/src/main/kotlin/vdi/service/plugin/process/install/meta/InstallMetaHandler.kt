package vdi.service.plugin.process.install.meta

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.io.path.absolutePathString
import kotlin.io.path.createFile
import kotlin.io.path.outputStream
import kotlin.time.Duration.Companion.seconds
import vdi.json.JSON
import vdi.model.DatasetMetaFilename
import vdi.service.plugin.metrics.ScriptMetrics
import vdi.service.plugin.model.PluginScriptError
import vdi.service.plugin.script.ScriptExecutor
import vdi.service.plugin.service.AbstractInstallHandler
import vdi.service.plugin.service.makeLogger
import vdi.util.io.LineListOutputStream
import vdi.util.io.LoggingOutputStream

open class InstallMetaHandler(context: InstallMetaContext, executor: ScriptExecutor, metrics: ScriptMetrics)
  : AbstractInstallHandler<Unit, InstallMetaContext>(context, executor, metrics, context.makeLogger())
{
  override suspend fun runJob() {
    val metaFile = workspace.resolve(DatasetMetaFilename)
      .createFile()
      .apply { outputStream().use { JSON.writeValue(it, scriptContext.metadata) } }

    val timer = metrics.installMetaScriptDuration.startTimer()

    executor.executeScript(
      scriptContext.scriptConfig.path,
      workspace,
      arrayOf(datasetID.toString(), metaFile.absolutePathString()),
      buildScriptEnv()
    ) {
      coroutineScope {
        val warnings = ArrayList<String>(4)

        val logJob = launch { LoggingOutputStream(logger).use { scriptStdErr.transferTo(it) } }
        val warnJob = launch { LineListOutputStream(warnings).use { scriptStdOut.transferTo(it) } }

        waitFor(scriptContext.scriptConfig.maxDuration)

        logJob.join()
        warnJob.join()

        val status = InstallMetaScript.ExitCode.fromCode(exitCode())

        metrics.installMetaCalls.labelValues(status.displayName).inc()

        when (status) {
          InstallMetaScript.ExitCode.Success -> {
            val dur = timer.observeDuration()
            logger.info("script completed successfully in {}", dur.seconds)
          }

          InstallMetaScript.ExitCode.ValidationError -> {
            logger.info("validation failed with")

          }

          else -> throw PluginScriptError(scriptContext.scriptConfig, status)
        }
      }
    }
  }
}