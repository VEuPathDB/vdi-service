package vdi.service.plugin.service

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.nio.file.Path
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import kotlin.io.path.*
import kotlin.time.Duration.Companion.seconds
import vdi.json.JSON
import vdi.model.DatasetManifestFilename
import vdi.model.DatasetMetaFilename
import vdi.model.OriginTimestamp
import vdi.model.data.DatasetFileInfo
import vdi.model.data.DatasetManifest
import vdi.service.plugin.consts.ExitStatus
import vdi.service.plugin.consts.ScriptEnvKey
import vdi.service.plugin.metrics.ScriptMetrics
import vdi.service.plugin.script.PluginScript
import vdi.service.plugin.script.PluginScriptException
import vdi.service.plugin.script.ScriptExecutor
import vdi.service.plugin.server.context.ImportContext
import vdi.service.plugin.util.Base36
import vdi.util.io.LineListOutputStream
import vdi.util.io.LoggingOutputStream
import vdi.util.zip.CompressionLevel
import vdi.util.zip.compress
import vdi.util.zip.zipEntries


/**
 * Executes 'import' dataset preprocessing and validation.
 *
 * @see [run] for more details.
 */
class ImportHandler(context: ImportContext, executor: ScriptExecutor, metrics: ScriptMetrics)
: AbstractScriptHandler<Path, ImportContext>(context, executor, metrics, context.markedLogger()) {
  private val inputDirectory: Path = workspace.resolve(InputDirName).createDirectory()

  private val outputDirectory: Path = workspace.resolve(OutputDirName).createDirectory()

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
  override suspend fun runJob(): Path {
    val inputFiles   = unpackInput()
    val warnings     = executeScript()
    val outputFiles  = collectOutputFiles()
    val dataFilesZip = workspace.resolve(DataZipName)
      .also { it.compress(outputFiles) }

    inputDirectory.deleteRecursively()

    return workspace.resolve(OutputFileName)
      .also { it.compress(
        listOf(writeManifestFile(inputFiles, outputFiles), writeWarningFile(warnings), dataFilesZip),
        CompressionLevel(0u)
      ) }
      .also { outputDirectory.deleteRecursively() }
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
    scriptContext.payload.zipEntries().forEach { (entry, inp) ->
      val file = inputDirectory.resolve(entry.name)
      file.outputStream().use { out -> inp.transferTo(out) }
    }

    scriptContext.payload.deleteExisting()

    val inputFiles = inputDirectory.listDirectoryEntries()

    // Write this out AFTER we gather the input files as this is not a file we
    // want to track in the manifest.
    writeInputMetaFile()

    if (inputFiles.isEmpty())
      throw EmptyInputError()

    return inputFiles.map { it to it.fileSize() }
  }

  /**
   * Executes the import script.
   *
   * If the import script fails execution this method will raise an appropriate
   * exception.
   *
   * If the import script returns a status code of
   * [ExitStatus.Import.Success], this method returns normally.
   *
   * If the import script returns a status code of
   * [ExitStatus.Import.ValidationFailure], this method will throw a
   * [ValidationError] exception.
   *
   * If the import script returns any other status code, this method will throw
   * an [PluginScriptException].
   *
   * @return A collection of warnings raised by the import script during its
   * execution.
   */
  private suspend fun executeScript(): Collection<String> {
    val timer = metrics.importScriptDuration.startTimer()

    logger.info("starting import script execution")
    val warnings = executor.executeScript(
      script.path,
      workspace,
      arrayOf(inputDirectory.absolutePathString(), outputDirectory.absolutePathString()),
      buildScriptEnv(),
    ) {
      coroutineScope {
        val warnings = ArrayList<String>(8)

        val j1 = launch { LineListOutputStream(warnings).use { scriptStdOut.transferTo(it) } }
        val j2 = launch { LoggingOutputStream(logger).use { scriptStdErr.transferTo(it) } }

        waitFor(script.maxDuration)

        j1.join()
        j2.join()

        val importStatus = ExitStatus.Import.fromCode(exitCode())

        metrics.importScriptCalls.labelValues(importStatus.metricFriendlyName).inc()

        when (importStatus) {
          ExitStatus.Import.Success -> {
            val dur = timer.observeDuration()
            logger.info("script completed successfully in {}", dur.seconds)
          }

          ExitStatus.Import.ValidationFailure -> {
            logger.info("script rejected dataset with {} validation error(s)", warnings.size)
            throw ValidationError(warnings)
          }

          else -> {
            val err = "script failed with exit code ${exitCode()}"
            logger.error(err)
            throw PluginScriptException(PluginScript.Import, err)
          }
        }

        warnings
      }
    }

    return warnings
  }

  private fun collectOutputFiles() : MutableCollection<Path> {
    // Collect a list of the files that the import script spit out.
    val outputFiles = outputDirectory.listDirectoryEntries()
      .toMutableList()

    // Ensure that _something_ was produced.
    if (outputFiles.isEmpty())
      throw PluginScriptException(PluginScript.Import, "script produced no output files")

    if (outputFiles.any { it.name.startsWith("vdi-") })
      throw PluginScriptException(PluginScript.Import, "script produced 'vdi-' prefixed file(s)")

    return outputFiles
  }

  private fun writeManifestFile(inputFiles: Collection<Pair<Path, Long>>, outputFiles: Collection<Path>) =
    outputDirectory.resolve(DatasetManifestFilename)
      .createFile()
      .apply { outputStream().use { JSON.writeValue(it, buildManifest(inputFiles, outputFiles)) } }

  private fun writeInputMetaFile() =
    inputDirectory.resolve(DatasetMetaFilename)
      .createFile()
      .apply { outputStream().use { JSON.writeValue(it, scriptContext.request.meta) } }

  private fun writeWarningFile(warnings: Collection<String>) =
    outputDirectory.resolve(WarningFileName)
      .createFile()
      .apply { outputStream().use { JSON.writeValue(it, WarningsFile(warnings)) } }

  private fun buildManifest(inputFiles: Collection<Pair<Path, Long>>, outputFiles: Collection<Path>) =
    DatasetManifest(
      inputFiles = inputFiles.map { DatasetFileInfo(it.first.name, it.second.toULong()) },
      dataFiles  = outputFiles.map { DatasetFileInfo(it.name, it.fileSize().toULong()) },
    )

  class EmptyInputError : RuntimeException("input archive contained no files")

  class ValidationError(val warnings: Collection<String>) : RuntimeException()

  data class WarningsFile(val warnings: Collection<String>)
}
