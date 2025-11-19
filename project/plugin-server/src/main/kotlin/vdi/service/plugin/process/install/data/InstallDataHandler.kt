package vdi.service.plugin.process.install.data

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.time.Duration.Companion.seconds
import vdi.io.plugin.responses.ValidationResponse
import vdi.json.JSON
import vdi.model.DatasetMetaFilename
import vdi.model.meta.DatasetMetadata
import vdi.service.plugin.metrics.ScriptMetrics
import vdi.service.plugin.model.MissingDependencyError
import vdi.service.plugin.model.PluginScriptError
import vdi.service.plugin.model.ValidationError
import vdi.service.plugin.process.install.meta.InstallMetaScript
import vdi.service.plugin.script.ScriptExecutor
import vdi.service.plugin.service.*
import vdi.util.io.LineListOutputStream
import vdi.util.io.LoggingOutputStream
import vdi.util.zip.unzip

class InstallDataHandler(context: InstallDataContext, executor: ScriptExecutor, metrics: ScriptMetrics)
  : AbstractInstallHandler<ValidationResponse, InstallDataContext>(context, executor, metrics, context.dataLogger())
{
  init {
    scriptContext.installPath.also {
      if (it.exists()) {
        val msg = "dataset install directory already exists: ${it.relativeTo(it.parent.parent.parent)}"
        logger.error(msg)
        throw InstallDirConflictError(msg)
      }
    }
  }

  override suspend fun runJob(): ValidationResponse {
    val installWorkspace = prepareInstallWorkspace()
    val metaFile = writeMetaFile(installWorkspace, scriptContext.metadata)
    val warnings = ArrayList<String>(4)

    runCheckDependencies(metaFile)

    runInstallData(installWorkspace, warnings)

    runInstallMeta(metaFile, scriptContext.metaConfig)

    return newValidationResponse(true, warnings)
  }

  private fun prepareInstallWorkspace(): Path {
    val installWorkspace = workspace.resolve(InstallDirName)

    logger.debug("creating install data workspace {}", installWorkspace)
    installWorkspace.createDirectory()

    logger.debug("unpacking {} as a .zip file", scriptContext.payload)
    scriptContext.payload.unzip(installWorkspace)
    scriptContext.payload.deleteIfExists()

    return installWorkspace
  }

  private suspend fun runCheckDependencies(metaFile: Path) {
    if (scriptContext.metadata.dependencies.isNotEmpty())
      return

    val log = scriptContext.compatLogger()

    logger.info("executing check-compatibility")

    val timer = metrics.checkCompatScriptDuration.startTimer()
    val warnings = ArrayList<String>()
    val meta = JSON.readValue<DatasetMetadata>(metaFile.inputStream())

    executor.executeScript(
      scriptContext.compatConfig.path,
      workspace,
      emptyArray(),
      buildScriptEnv()
    ) {
      coroutineScope {

        val logJob = launch {
          LoggingOutputStream(log)
            .use { scriptStdErr.transferTo(it) }
        }

        val warnJob = launch {
          LineListOutputStream(warnings)
            .use { scriptStdOut.transferTo(it) }
        }

        val osw = OutputStreamWriter(scriptStdIn)

        try {
          for (dep in meta.dependencies)
            osw.appendLine("${dep.identifier}\t${dep.version}")

          osw.flush()
        } catch (e: IOException) {
          log.error("Encountered error while attempting to write to process stdin:", e)

          if (isAlive())
            throw e
        } finally {
          try {
            osw.close()
          } catch (e: IOException) {
            log.error("Encountered error while attempting to close process stdin:", e)

            if (isAlive())
              throw e
          }
        }

        waitFor(scriptContext.compatConfig.maxDuration)

        logJob.join()
        warnJob.join()

        val compatStatus = CheckCompatibilityScript.ExitCode.fromCode(exitCode())

        metrics.checkCompatCalls.labelValues(compatStatus.displayName).inc()

        when (compatStatus) {
          CheckCompatibilityScript.ExitCode.Success -> {
            val dur = timer.observeDuration()
            log.info("script completed successfully in {}", dur.seconds)
          }

          CheckCompatibilityScript.ExitCode.CompatibilityError -> {
            log.info("script completed with status 'incompatible'")
            throw MissingDependencyError(warnings)
          }

          else -> throw PluginScriptError(scriptContext.compatConfig, compatStatus)
            .also { log.error(it.message) }
        }
      }
    }
  }

  private suspend fun runInstallMeta(metaFile: Path, script: InstallMetaScript) {
    val log = scriptContext.metaLogger()
    val timer = metrics.installMetaScriptDuration.startTimer()
    val warnings = ArrayList<String>(8)

    logger.info("executing install-meta (for install-data)")

    executor.executeScript(
      script.path,
      workspace,
      arrayOf(datasetID.toString(), metaFile.absolutePathString()),
      buildScriptEnv()
    ) {
      coroutineScope {
        val logJob = launch { LoggingOutputStream(log).use { scriptStdErr.transferTo(it) } }
        val warnJob = launch { LineListOutputStream(warnings).use { scriptStdOut.transferTo(it) } }

        waitFor(script.maxDuration)

        logJob.join()
        warnJob.join()

        val status = InstallMetaScript.ExitCode.fromCode(exitCode())

        metrics.installMetaCalls.labelValues(status.displayName).inc()

        when (status) {
          InstallMetaScript.ExitCode.Success -> {
            val dur = timer.observeDuration()
            log.info("script completed successfully in {}", dur.seconds)
          }

          InstallMetaScript.ExitCode.ValidationError -> {
            log.info("script failed validation with {} warnings", warnings.size)
            throw ValidationError(newValidationResponse(false, warnings))
          }

          else -> throw PluginScriptError(script, status)
            .also { log.error(it.message) }
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

        val installStatus = InstallDataScript.ExitCode.fromCode(exitCode())

        metrics.installDataCalls.labelValues(installStatus.displayName).inc()

        when (installStatus) {
          InstallDataScript.ExitCode.Success -> {
            val dur = timer.observeDuration()
            logger.info("script completed successfully in {}", dur.seconds)
          }

          InstallDataScript.ExitCode.ValidationError -> {
            logger.info("script refused to install dataset for validation errors")
            throw ValidationError(newValidationResponse(false, warnings))
          }

          else -> throw PluginScriptError(scriptContext.dataConfig, installStatus)
            .also { logger.error(it.message) }
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

  class InstallDirConflictError(msg: String): RuntimeException(msg)
}

