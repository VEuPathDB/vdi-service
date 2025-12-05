package vdi.service.plugin.process.install.data

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.time.Duration.Companion.seconds
import vdi.io.plugin.responses.InstallDataResponse
import vdi.io.plugin.responses.MissingDependencyResponse
import vdi.io.plugin.responses.ServerErrorResponse
import vdi.json.JSON
import vdi.logging.mark
import vdi.model.DatasetMetaFilename
import vdi.model.meta.DatasetMetadata
import vdi.service.plugin.metrics.ScriptMetrics
import vdi.service.plugin.process.install.meta.InstallMetaHandler
import vdi.service.plugin.process.install.meta.InstallMetaHandler.Companion.InstallMetaJob
import vdi.service.plugin.script.ScriptExecutor
import vdi.service.plugin.script.ScriptProcess
import vdi.service.plugin.script.newErrorResponse
import vdi.service.plugin.service.AbstractInstallHandler
import vdi.service.plugin.service.InstallDirName
import vdi.util.io.LineListOutputStream
import vdi.util.io.LoggingOutputStream
import vdi.util.zip.unzip

internal class InstallDataHandler
private constructor(
  context:  InstallDataContext,
  executor: ScriptExecutor,
  metrics:  ScriptMetrics,
)
: AbstractInstallHandler<InstallDataResponse, InstallDataContext>(context, executor, metrics)
{
  companion object {
    context(ctx: InstallDataContext)
    suspend fun run(executor: ScriptExecutor, metrics: ScriptMetrics) =
      InstallDataHandler(ctx, executor, metrics).run()
  }

  override suspend fun runJob(): InstallDataResponse {
    scriptContext.installPath.also {
      if (it.exists()) {
        val msg = "dataset install directory already exists: ${it.relativeTo(it.parent.parent.parent)}"
        logger.error(msg)
        return ServerErrorResponse(msg)
      }
    }

    val installWorkspace = prepareInstallWorkspace()
    val metaFile = writeMetaFile(installWorkspace, scriptContext.metadata)
    val warnings = ArrayList<String>(4)

    runCheckDependencies(metaFile)
      ?.also { return it }

    runInstallData(installWorkspace, warnings)
      ?.also { return it }

    InstallMetaHandler.runJob(InstallMetaJob(
      executor,
      workspace,
      metaFile,
      scriptContext,
      metrics,
      buildScriptEnv(),
      metaLogger(),
    ))

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

  private suspend fun runCheckDependencies(metaFile: Path): InstallDataResponse? {
    if (scriptContext.metadata.dependencies.isEmpty())
      return null

    val log = compatLogger()

    logger.info("executing check-compatibility")

    val timer = metrics.checkCompatScriptDuration.startTimer()
    val warnings = ArrayList<String>()
    val meta = JSON.readValue<DatasetMetadata>(metaFile.inputStream())

    return executor.executeScript(
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

        writeDependencies(meta, log)?.also { return@coroutineScope it }

        waitFor(scriptContext.compatConfig.maxDuration)

        logJob.join()
        warnJob.join()

        val compatStatus = CheckCompatibilityScript.ExitCode.fromCode(exitCode())

        metrics.checkCompatCalls.labelValues(compatStatus.displayName).inc()

        return@coroutineScope when (compatStatus) {
          CheckCompatibilityScript.ExitCode.Success -> {
            val dur = timer.observeDuration()
            log.info("script completed successfully in {}", dur.seconds)
            null
          }

          CheckCompatibilityScript.ExitCode.CompatibilityError -> {
            log.info("script completed with status 'incompatible'")
            MissingDependencyResponse(warnings)
          }

          else -> scriptContext.compatConfig.newErrorResponse(compatStatus.code)
            .also { log.error(it.message) }
        }
      }
    }
  }

  private fun ScriptProcess.writeDependencies(meta: DatasetMetadata, log: Logger): InstallDataResponse? {
    val osw = OutputStreamWriter(scriptStdIn)

    try {
      for (dep in meta.dependencies)
        osw.appendLine("${dep.identifier}\t${dep.version}")

      osw.flush()
    } catch (e: IOException) {
      log.error("Encountered error while attempting to write to process stdin:", e)

      // If the process is still running, then this is an error.  If the
      // process ended, then this may be ignored.
      if (isAlive()) {
        kill()
        return ServerErrorResponse(e.message!!)
      }
    } finally {
      try {
        osw.close()
      } catch (e: IOException) {
        log.error("Encountered error while attempting to close process stdin:", e)

        // If the process is still running, then this is an error.  If the
        // process ended, then this may be ignored.
        if (isAlive()) {
          kill()
          throw e
        }
      }
    }

    return null
  }

  private suspend fun runInstallData(installDir: Path, warnings: MutableList<String>): InstallDataResponse? {
    logger.info("executing install-data")
    val timer = metrics.installDataScriptDuration.startTimer()
    return executor.executeScript(
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
            null
          }

          InstallDataScript.ExitCode.ValidationError -> {
            logger.info("script refused to install dataset for validation errors")
            newValidationResponse(false, warnings)
          }

          else -> scriptContext.dataConfig.newErrorResponse(installStatus.code)
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

  private fun metaLogger() = logger.mark(scriptName = scriptContext.metaConfig.kind.name)
  private fun compatLogger() = logger.mark(scriptName = scriptContext.compatConfig.kind.name)
}

