package vdi.service.importing

import io.ktor.server.application.*
import vdi.conf.Configuration.ServiceConfiguration
import vdi.conf.Consts
import vdi.util.unpackAsTarGZ
import java.io.File
import java.util.concurrent.TimeUnit

fun ApplicationCall.processImport(vdiID: String, parentDir: File, inputArchive: File) {

  val scriptInputDirectory  = parentDir.resolve("plugin-input")
  val scriptOutputDirectory = parentDir.resolve("plugin-output")

  scriptInputDirectory.mkdir()
  scriptOutputDirectory.mkdir()

  inputArchive.unpackAsTarGZ(into = scriptInputDirectory, overwrite = false)
  inputArchive.delete()

  with(ProcessBuilder(
    ServiceConfiguration.importScriptPath,
    scriptInputDirectory.path,
    scriptOutputDirectory.path,
  ).apply {
    directory(parentDir)
  }.start()) {
    when (waitFor()) {
      Consts.IMPORT_SCRIPT_EXIT_SUCCESS_CODE -> TODO("SUCCESS")
      Consts.IMPORT_SCRIPT_EXIT_FAIL_VALIDATION_CODE -> TODO("Failed validation")
      Consts.IMPORT_SCRIPT_EXIT_FAIL_TRANSFORMATION_CODE -> TODO("Failed transformation")
      else -> TODO("Failed unexpected")
    }
  }

  TODO("""
    - Create an 'input' directory for the handler plugin
    - unpack the input archive to the `input` directory
    - delete the input archive file
    - create an `output` directory for the handler plugin
    - call the handler plugin with the created input and output directories
    - create a manifest.json
    - create a meta.json (HOW????)
    - package the output directory into an archive
    - return the archive
  """.trimIndent())
}