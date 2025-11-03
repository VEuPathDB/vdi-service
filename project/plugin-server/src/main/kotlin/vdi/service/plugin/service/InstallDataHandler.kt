package vdi.service.plugin.service

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.time.Duration.Companion.seconds
import vdi.json.JSON
import vdi.model.DatasetMetaFilename
import vdi.model.data.DatasetMetadata
import vdi.service.plugin.consts.ExitStatus
import vdi.service.plugin.metrics.ScriptMetrics
import vdi.service.plugin.script.PluginScript
import vdi.service.plugin.script.PluginScriptException
import vdi.service.plugin.script.ScriptExecutor
import vdi.service.plugin.server.context.InstallDataContext
import vdi.util.io.LineListOutputStream
import vdi.util.io.LoggingOutputStream
import vdi.util.zip.unzip

class InstallDataHandler(context: InstallDataContext, executor: ScriptExecutor, metrics: ScriptMetrics)
: AbstractInstallHandler<List<String>, InstallDataContext>(context, executor, metrics, context.markedLogger()) {
  init {
    scriptContext.installPath.also {
      if (it.exists()) {
        val msg = "dataset install directory already exists: ${it.relativeTo(it.parent.parent.parent)}"
        logger.error(msg)
        throw InstallDirConflictError(msg)
      }
    }
  }

  override suspend fun runJob() : List<String> {
    val installWorkspace = workspace.resolve(InstallDirName)
    val warnings = ArrayList<String>(4)

    logger.debug("creating install data workspace {}", installWorkspace)
    installWorkspace.createDirectory()

    logger.debug("unpacking {} as a .zip file", scriptContext.payload)
    scriptContext.payload.unzip(installWorkspace)
    scriptContext.payload.deleteIfExists()

    val metaFile = writeMetaFile(installWorkspace, scriptContext.metadata)
    runInstallMeta(metaFile)

    if (scriptContext.metadata.dependencies.isNotEmpty())
      runCheckDependencies(metaFile)

    metaFile.deleteIfExists()

    runInstallData(installWorkspace, warnings)

    return warnings
  }

  private suspend fun runInstallMeta(metaFile: Path) {
    val log = scriptContext.metaLogger()
    val timer = metrics.installMetaScriptDuration.startTimer()

    logger.info("executing install-meta (for install-data)")

    executor.executeScript(scriptContext.metaConfig.path, workspace, arrayOf(datasetID.toString(), metaFile.absolutePathString()), buildScriptEnv()) {
      coroutineScope {
        val logJob = launch { LoggingOutputStream(log).use { scriptStdErr.transferTo(it) } }

        waitFor(scriptContext.metaConfig.maxDuration)

        logJob.join()

        metrics.installMetaCalls.labelValues(ExitStatus.InstallMeta.fromCode(exitCode()).metricFriendlyName).inc()

        when (exitCode()) {
          0 -> {
            val dur = timer.observeDuration()
            log.info("script completed successfully in {}", dur.seconds)
          }

          else -> {
            val err = "script failed with exit code ${exitCode()}"
            log.error(err)
            throw PluginScriptException(PluginScript.InstallMeta, err)
          }
        }
      }
    }
  }

  private suspend fun runCheckDependencies(metaFile: Path) {
    val log = scriptContext.compatLogger()

    logger.info("executing check-compatibility")

    val timer    = metrics.checkCompatScriptDuration.startTimer()
    val warnings = ArrayList<String>()
    val meta     = JSON.readValue<DatasetMetadata>(metaFile.inputStream())

    executor.executeScript(
      scriptContext.compatConfig.path,
      workspace,
      emptyArray(),
      buildScriptEnv()
    ) {
      coroutineScope {

        val logJob  = launch { LoggingOutputStream(log).use { scriptStdErr.transferTo(it) } }
        val warnJob = launch { LineListOutputStream(warnings).use { scriptStdOut.transferTo(it) } }

        val osw = OutputStreamWriter(scriptStdIn)
        try {
          for (dep in meta.dependencies)
            osw.appendLine("${dep.identifier}\t${dep.version}")
          osw.flush()
        } catch (e: IOException) {
          log.error("Encountered error while attempting to write to process stdin:", e)
          if (isAlive()) {
            throw e
          }
        } finally {
          try {
            osw.close()
          } catch (e: IOException) {
            log.error("Encountered error while attempting to close process stdin:", e)
            if (isAlive()) {
              throw e
            }
          }
        }

        waitFor(scriptContext.compatConfig.maxDuration)

        logJob.join()
        warnJob.join()

        val compatStatus = ExitStatus.CheckCompatibility.fromCode(exitCode())

        metrics.checkCompatCalls.labelValues(compatStatus.metricFriendlyName).inc()

        when (compatStatus) {
          ExitStatus.CheckCompatibility.Success -> {
            val dur = timer.observeDuration()
            log.info("script completed successfully in {}", dur.seconds)
          }

          ExitStatus.CheckCompatibility.Incompatible -> {
            log.info("script completed with status 'incompatible'")
            throw CompatibilityError(warnings)
          }

          else -> {
            val err = "script failed with exit code ${exitCode()}"
            log.error(err)
            throw PluginScriptException(PluginScript.CheckCompatibility, err)
          }
        }
      }
    }
  }

  private suspend fun runInstallData(installDir: Path, warnings: MutableList<String>) {
    logger.info("executing install-data")
    val timer = metrics.installDataScriptDuration.startTimer()
    executor.executeScript(
      scriptContext.dataConfig.path,
      workspace,
      arrayOf(datasetID.toString(), installDir.absolutePathString()),
      buildScriptEnv()
    ) {
      coroutineScope {
        val job1 = launch { LoggingOutputStream(logger).use { scriptStdErr.transferTo(it) } }
        val job2 = launch { LineListOutputStream(warnings).use { scriptStdOut.transferTo(it) } }

        waitFor(scriptContext.dataConfig.maxDuration)

        job1.join()
        job2.join()

        val installStatus = ExitStatus.InstallData.fromCode(exitCode())

        metrics.installDataCalls.labelValues(installStatus.metricFriendlyName).inc()

        when (installStatus) {
          ExitStatus.InstallData.Success -> {
            val dur = timer.observeDuration()
            logger.info("script completed successfully in {}", dur.seconds)
          }

          ExitStatus.InstallData.ValidationFailure -> {
            logger.info("script refused to install dataset for validation errors")
            throw ValidationError(warnings)
          }

          else -> {
            val err = "script failed with exit code ${exitCode()}"
            logger.error(err)
            throw PluginScriptException(PluginScript.InstallData, err)
          }
        }
      }
    }
  }

  private fun writeMetaFile(installDir: Path, meta: DatasetMetadata): Path {
    val metaFile = installDir.resolve(DatasetMetaFilename)
    metaFile.createFile()
    metaFile.outputStream().buffered().use { JSON.writeValue(it, meta) }
    return metaFile
  }

  class ValidationError(val warnings: Collection<String>) : RuntimeException()

  class CompatibilityError(val warnings: Collection<String>) : RuntimeException()

  class InstallDirConflictError(msg: String) : RuntimeException(msg)
}
