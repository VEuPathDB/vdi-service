package vdi.service.plugin.process.uninstall

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.moveTo
import kotlin.time.Duration.Companion.seconds
import vdi.service.plugin.metrics.ScriptMetrics
import vdi.service.plugin.model.PluginScriptError
import vdi.service.plugin.script.ScriptExecutor
import vdi.service.plugin.service.AbstractInstallHandler
import vdi.service.plugin.service.makeLogger
import vdi.util.fmt.RFC3339
import vdi.util.io.LoggingOutputStream

class UninstallDataHandler(context: UninstallDataContext, executor: ScriptExecutor, metrics: ScriptMetrics)
  : AbstractInstallHandler<Unit, UninstallDataContext>(context, executor, metrics, context.makeLogger())
{
  private inline val script
    get() = scriptContext.scriptConfig

  override suspend fun runJob() {
    val timer = metrics.uninstallScriptDuration.startTimer()

    executor.executeScript(script.path, workspace, arrayOf(datasetID.toString()), buildScriptEnv()) {
      coroutineScope {
        val logJob = launch { LoggingOutputStream(logger).use { scriptStdErr.transferTo(it) } }

        waitFor(script.maxDuration)

        logJob.join()

        val exitStatus = UninstallScript.ExitCode.fromCode(exitCode())

        metrics.uninstallCalls.labelValues(exitStatus.displayName).inc()

        when (exitStatus) {
          UninstallScript.ExitCode.Success -> {
            val dur = timer.observeDuration()
            logger.info("uninstall script completed successfully in {}", dur.seconds)
            wipeDatasetDir()
          }

          else -> throw PluginScriptError(script, exitStatus).also { logger.error(it.message) }
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