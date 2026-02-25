package vdi.service.plugin.process.preprocess

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.io.asOutputStream
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import kotlin.io.path.*
import kotlin.time.Duration.Companion.seconds
import vdi.io.plugin.FileName
import vdi.io.plugin.responses.ErrorResponse
import vdi.io.plugin.responses.ImportResponse
import vdi.io.plugin.responses.ValidationResponse
import vdi.json.JSON
import vdi.model.DatasetManifestFilename
import vdi.model.DatasetMetaFilename
import vdi.model.OriginTimestamp
import vdi.model.meta.DatasetFileInfo
import vdi.model.meta.DatasetManifest
import vdi.service.plugin.consts.ScriptEnvKey
import vdi.service.plugin.metrics.ScriptMetrics
import vdi.service.plugin.script.ScriptExecutor
import vdi.service.plugin.script.newErrorResponse
import vdi.service.plugin.service.AbstractScriptHandler
import vdi.service.plugin.service.InputDirName
import vdi.service.plugin.service.OutputDirName
import vdi.service.plugin.service.OutputFileName
import vdi.service.plugin.util.Base36
import vdi.service.plugin.util.fileSize
import vdi.service.plugin.util.toJavaPath
import vdi.util.fn.Either
import vdi.util.fn.leftOr
import vdi.util.io.LineListOutputStream
import vdi.util.io.LoggingOutputStream
import vdi.util.zip.CompressionLevel
import vdi.util.zip.compress
import vdi.util.zip.zipEntries
import java.nio.file.Path as JavaPath

/**
 * Executes 'import' dataset preprocessing and validation.
 *
 * @see [vdi.service.plugin.service.AbstractScriptHandler.run] for more details.
 */
internal class ImportHandler
private constructor(
  context:  ImportContext,
  executor: ScriptExecutor,
  metrics:  ScriptMetrics,
)
: AbstractScriptHandler<Either<JavaPath, ImportResponse>, ImportContext>(context, executor, metrics)
{
  companion object {
    context(ctx: ImportContext)
    suspend fun run(executor: ScriptExecutor, metrics: ScriptMetrics) =
      ImportHandler(ctx, executor, metrics).run()
  }

  private val inputDirectory  = Path(workspace, InputDirName).also(SystemFileSystem::createDirectories)
  private val outputDirectory = Path(workspace, OutputDirName).also(SystemFileSystem::createDirectories)

  private inline val script
    get() = scriptContext.scriptConfig

  /**
   * Performs the import preprocessing steps for a target dataset.
   *
   * The steps involved are:
   * 1. Unpack the posted input zip.
   * 2. Execute the plugin 'import' script on the input files.
   * 3. Zip the outputs from the plugin 'import' script.
   * 4. Write a manifest of the import inputs and outputs.
   * 5. Write a list of import warnings.
   * 6. Return an uncompressed zip stream containing the manifest, warnings, and
   *    compressed results.
   */
  @OptIn(ExperimentalPathApi::class)
  override suspend fun runJob(): Either<JavaPath, ImportResponse> {
    val inputFiles = unpackInput()
    val warnings = executeScript()

    // If validation failed, or some other error occurred, stop here.
    if (warnings !is ValidationResponse || !warnings.isValid)
      return Either.right(warnings)

    val outputFiles = collectOutputFiles()
      .leftOr { return Either.right(it) }

    val dataFilesZip = Path(workspace, FileName.DataFile)
      .toJavaPath()
      .also { it.compress(outputFiles.map(Path::toJavaPath)) }

    inputDirectory.toJavaPath().deleteRecursively()

    return Path(workspace, OutputFileName)
      .toJavaPath()
      .also { it.compress(
        listOf(
          writeManifestFile(inputFiles, outputFiles),
          writeWarningFile(warnings),
          dataFilesZip,
        ),
        CompressionLevel(0u)
      ) }
      .also { outputDirectory.toJavaPath().deleteRecursively() }
      .let { Either.left(it) }
  }

  override fun appendScriptEnv(env: MutableMap<String, String>) {
    super.appendScriptEnv(env)
    env[ScriptEnvKey.ImportID] = generateImportID()
  }

  @Suppress("NOTHING_TO_INLINE")
  private inline fun generateImportID() =
    Base36.encodeToString(OriginTimestamp.until(OffsetDateTime.now(), ChronoUnit.SECONDS).toULong()) +
      Base36.encodeToString(scriptContext.request.importIndex.toULong())


  /**
   * Unpacks the given input archive into the input directory and ensures that
   * the archive contained at least one input file.
   *
   * @return A collection of [Pair]s containing the paths to the files in the
   * input directory paired with the sizes of those files.  The sizes are used
   * to build the `vdi-manifest.json` file.
   */
  private fun unpackInput(): Collection<Pair<Path, Long>> {
    val jvmImportZip = scriptContext.importReadyZip.toJavaPath()

    jvmImportZip
      .zipEntries()
      .forEach { (entry, inp) ->
        val file = Path(inputDirectory, entry.name).toJavaPath()
        file.outputStream().use { out -> inp.transferTo(out) }
      }

    jvmImportZip.deleteExisting()

    val inputFiles = SystemFileSystem.list(inputDirectory)

    // Write this out AFTER we gather the input files as this is not a file we
    // want to track in the manifest.
    writeInputMetaFile()

    return inputFiles.map { it to it.fileSize()!! }
  }

  /**
   * Executes the import script.
   *
   * If the import script fails execution this method will raise an appropriate
   * exception.
   *
   * If the import script returns a status code of
   * [vdi.service.plugin.consts.ExitStatus.Import.Success], this method returns normally.
   *
   * @return A collection of warnings raised by the import script during its
   * execution.
   */
  private suspend fun executeScript(): ImportResponse {
    val timer = metrics.importScriptDuration.startTimer()

    logger.info("starting import script execution")
    return executor.executeScript(
      script.path,
      workspace,
      arrayOf(inputDirectory.toJavaPath().absolutePathString(), outputDirectory.toJavaPath().absolutePathString()),
      buildScriptEnv(),
    ) {
      coroutineScope {
        val warnings = ArrayList<String>(8)

        val j1 = launch { LineListOutputStream(warnings).use { scriptStdOut.transferTo(it) } }
        val j2 = launch { LoggingOutputStream(logger).use { scriptStdErr.transferTo(it) } }

        waitFor(script.maxDuration)

        j1.join()
        j2.join()

        val importStatus = ImportScript.ExitCode.fromCode(exitCode())

        metrics.importScriptCalls.labelValues(importStatus.displayName).inc()

        when (importStatus) {
          ImportScript.ExitCode.Success -> {
            val dur = timer.observeDuration()
            logger.info("script completed successfully in {}", dur.seconds)
            newValidationResponse(true, warnings)
          }

          ImportScript.ExitCode.ValidationError -> {
            logger.info("script rejected dataset with {} validation error(s)", warnings.size)
            newValidationResponse(false, warnings)
          }

          else -> scriptContext.scriptConfig.newErrorResponse(importStatus.code)
            .also { logger.error(it.message) }
        }
      }
    }
  }

  private fun collectOutputFiles(): Either<MutableCollection<Path>, ErrorResponse> {
    // Collect a list of the files that the import script spit out.
    val outputFiles = SystemFileSystem.list(outputDirectory)
      .toMutableList()

    // Ensure that _something_ was produced.
    if (outputFiles.isEmpty())
      return Either.right(script.newErrorResponse(0u, "script produced no output files"))

    if (outputFiles.any { it.name.startsWith("vdi-") })
      return Either.right(script.newErrorResponse(0u, "script produced 'vdi-' prefixed file(s)"))

    return Either.left(outputFiles)
  }

  private fun writeManifestFile(inputFiles: Collection<Pair<Path, Long>>, outputFiles: Collection<Path>) =
    Path(outputDirectory, DatasetManifestFilename)
      .also { file -> SystemFileSystem.sink(file).buffered().use { sink ->
        JSON.writeValue(sink.asOutputStream(), buildManifest(inputFiles, outputFiles))
        sink.flush()
      } }
      .toJavaPath()

  private fun writeInputMetaFile() =
    Path(inputDirectory, DatasetMetaFilename)
      .also { file -> SystemFileSystem.sink(file).buffered().use { sink ->
        JSON.writeValue(sink.asOutputStream(), scriptContext.request.meta)
        sink.flush()
      } }
      .toJavaPath()

  private fun writeWarningFile(warnings: ValidationResponse) =
    Path(outputDirectory, FileName.WarningsFile)
      .also { file -> SystemFileSystem.sink(file).buffered().use { sink ->
        JSON.writeValue(sink.asOutputStream(), warnings)
      } }
      .toJavaPath()

  private fun buildManifest(inputFiles: Collection<Pair<Path, Long>>, outputFiles: Collection<Path>) =
    DatasetManifest(
      userUploadFiles = inputFiles.map { DatasetFileInfo(it.first.name, it.second.toULong()) },
      installReadyFiles = outputFiles.map { DatasetFileInfo(it.name, SystemFileSystem.metadataOrNull(it)!!.size.toULong()) },
    )
}

