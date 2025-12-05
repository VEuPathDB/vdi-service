package vdi.service.plugin.process.install.meta

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createFile
import kotlin.io.path.outputStream
import kotlin.time.Duration.Companion.seconds
import vdi.io.plugin.responses.ErrorResponse
import vdi.io.plugin.responses.InstallMetaResponse
import vdi.json.JSON
import vdi.model.DatasetMetaFilename
import vdi.model.Environment
import vdi.service.plugin.metrics.ScriptMetrics
import vdi.service.plugin.script.ScriptExecutor
import vdi.service.plugin.script.newErrorResponse
import vdi.service.plugin.server.context.InstallScriptContext
import vdi.service.plugin.service.AbstractInstallHandler
import vdi.util.io.LineListOutputStream
import vdi.util.io.LoggingOutputStream

internal class InstallMetaHandler
private constructor(
  context:  InstallMetaContext,
  executor: ScriptExecutor,
  metrics:  ScriptMetrics,
)
: AbstractInstallHandler<InstallMetaResponse?, InstallMetaContext>(context, executor, metrics)
{
  override suspend fun runJob(): InstallMetaResponse? {
    val metaFile = workspace.resolve(DatasetMetaFilename)
      .createFile()
      .apply { outputStream().use { JSON.writeValue(it, scriptContext.metadata) } }

    return runJob(InstallMetaJob(
      executor,
      workspace,
      metaFile,
      scriptContext,
      metrics,
      buildScriptEnv(),
      logger,
    ))
  }

  companion object {
    context(ctx: InstallMetaContext)
    suspend fun run(executor: ScriptExecutor, metrics: ScriptMetrics) =
      InstallMetaHandler(ctx, executor, metrics).run()

    data class InstallMetaJob(
      val executor:    ScriptExecutor,
      val workspace:   Path,
      val metaFile:    Path,
      val script:      InstallScriptContext<*, *>,
      val metrics:     ScriptMetrics,
      val environment: Environment,
      val logger:      Logger,
    )

    suspend fun runJob(job: InstallMetaJob): ErrorResponse? {
      val timer = job.metrics.installMetaScriptDuration.startTimer()

      return job.executor.executeScript(
        job.script.scriptConfig.path,
        job.workspace,
        arrayOf(
          job.script.datasetID.toString(),
          job.metaFile.absolutePathString(),
          job.script.dataPropertiesPath?.absolutePathString() ?: "",
        ),
        job.environment
      ) {
        coroutineScope {
          val warnings = ArrayList<String>(4)

          val logJob = launch { LoggingOutputStream(job.logger).use { scriptStdErr.transferTo(it) } }
          val warnJob = launch { LineListOutputStream(warnings).use { scriptStdOut.transferTo(it) } }

          waitFor(job.script.scriptConfig.maxDuration)

          logJob.join()
          warnJob.join()

          val status = InstallMetaScript.ExitCode.fromCode(exitCode())

          job.metrics.installMetaCalls.labelValues(status.displayName).inc()

          when (status) {
            InstallMetaScript.ExitCode.Success -> {
              val dur = timer.observeDuration()
              job.logger.info("script completed successfully in {}", dur.seconds)
              null
            }

            InstallMetaScript.ExitCode.ValidationError -> {
              job.logger.info("validation failed with")
              TODO("WAAAAA")
            }

            else -> job.script.scriptConfig.newErrorResponse(
              status.code,
            )
          }
        }
      }
    }
  }
}