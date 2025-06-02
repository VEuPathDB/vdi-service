package vdi.service.plugin.service

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.moveTo
import kotlin.time.Duration.Companion.seconds
import vdi.service.plugin.consts.ExitStatus
import vdi.service.plugin.metrics.ScriptMetrics
import vdi.service.plugin.script.PluginScript
import vdi.service.plugin.script.PluginScriptException
import vdi.service.plugin.script.ScriptExecutor
import vdi.service.plugin.server.context.UninstallDataContext
import vdi.util.fmt.RFC3339
import vdi.util.io.LoggingOutputStream

class UninstallDataHandler(context: UninstallDataContext, executor: ScriptExecutor, metrics: ScriptMetrics)
: AbstractInstallHandler<Unit, UninstallDataContext>(context, executor, metrics, context.markedLogger()) {
  private inline val script
    get() = scriptContext.scriptConfig

  override suspend fun runJob() {
    val timer = metrics.uninstallScriptDuration.startTimer()

    executor.executeScript(script.path, workspace, arrayOf(datasetID.toString()), buildScriptEnv()) {
      coroutineScope {
        val logJob = launch { LoggingOutputStream(logger).use { scriptStdErr.transferTo(it) } }

        waitFor(script.maxDuration)

        logJob.join()

        val installStatus = ExitStatus.UninstallData.fromCode(exitCode())

        metrics.uninstallCalls.labelValues(installStatus.metricFriendlyName).inc()

        when (installStatus) {
          ExitStatus.UninstallData.Success -> {
            val dur = timer.observeDuration()
            logger.info("uninstall script completed successfully in {}", dur.seconds)
            wipeDatasetDir()
          }

          else -> {
            val err = "uninstall script failed with exit code ${exitCode()}"
            logger.error(err)
            throw PluginScriptException(PluginScript.Uninstall, err)
          }
        }
      }
    }
  }

  @OptIn(ExperimentalPathApi::class)
  private fun wipeDatasetDir() {
    if (!installPath.exists()) {
      logger.info("dataset directory {} does not exist", installPath)
      return
    }

    logger.debug("attempting to delete dataset directory {}", installPath)

    installPath
      .moveTo(installPath.parent.resolve("deleting-$datasetID-${LocalDateTime.now().format(RFC3339)}"))
      .deleteRecursively()

    logger.info("deleted dataset directory {}", installPath)
  }
}
