package vdi.service.imports

import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import vdi.Const.ExitCode
import vdi.conf.Configuration.ServiceConfiguration
import vdi.server.model.SimpleErrorResponse
import vdi.server.model.WarningsListResponse
import vdi.server.respondJSON418
import vdi.server.respondJSON420
import vdi.server.respondJSON500
import vdi.service.model.DatasetManifest
import vdi.util.LoggingOutputStream
import vdi.util.toJSONString
import vdi.util.unpackAsTarGZ
import java.io.File
import java.util.concurrent.TimeUnit

private const val ManifestFileName = "manifest.json"

private val log = LoggerFactory.getLogger("Import Processing")

suspend fun ApplicationCall.processImport(vdiID: String, parentDir: File, inputArchive: File) {

  val scriptInputDirectory  = parentDir.resolve("plugin-input")
  val scriptOutputDirectory = parentDir.resolve("plugin-output")

  scriptInputDirectory.mkdir()
  scriptOutputDirectory.mkdir()

  inputArchive.unpackAsTarGZ(into = scriptInputDirectory, overwrite = false)
  inputArchive.delete()

  withContext(Dispatchers.IO) {
    with(
      ProcessBuilder(
        ServiceConfiguration.importScriptPath,
        scriptInputDirectory.path,
        scriptOutputDirectory.path,
      ).apply {
        directory(parentDir)
      }.start()
    ) {
      val warnings = ArrayList<String>(4)

  //    inputStream.lines()
      launch { LoggingOutputStream(log).use { errorStream.transferTo(it) } }

      if (!waitFor(ServiceConfiguration.importScriptMaxSeconds, TimeUnit.SECONDS)) {
        destroyForcibly()
        respondJSON500(SimpleErrorResponse("import script was forcibly killed due to timeout"))
        return@withContext
      }

      when (exitValue()) {
        ExitCode.ImportScriptSuccess -> {
          handleImportSuccess(vdiID, parentDir, scriptOutputDirectory)
          return@withContext
        }

        ExitCode.ImportScriptValidationFailure -> {
          respondJSON418(WarningsListResponse(warnings))
          return@withContext
        }

        ExitCode.ImportScriptTransformationFailure -> {
          respondJSON420(SimpleErrorResponse("could not transform given input into desired output format"))
          return@withContext
        }

        else -> {
          respondJSON500(SimpleErrorResponse("import script failed"))
          return@withContext
        }
      }
    }
  }
}

suspend fun ApplicationCall.handleImportSuccess(
  vdiID:     String,
  parentDir: File,
  inputDir:  File,
  outputDir: File,
) {
  val outputFiles = mutableListOf(*outputDir.list()!!)

  outputFiles.add(writeManifestFile(outputDir.resolve(ManifestFileName), inputDir.list()!!.asList(), outputFiles).name)


  // TODO: write meta.json
  // TODO: write warnings file
  // TODO: pack files into tar.gz
  // TODO: return tar.gz
}

suspend fun writeManifestFile(
  into:    File,
  inputs:  Collection<String>,
  outputs: Collection<String>,
) : File = withContext(Dispatchers.IO) {
  into.delete()
  into.createNewFile()
  into.bufferedWriter().use { it.write(DatasetManifest(inputs, outputs).toJSONString()) }
  into
}

suspend fun writeMeatFile(
  into: File,

)