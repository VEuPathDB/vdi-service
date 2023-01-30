package vdi.service.importing

import io.ktor.server.application.*
import vdi.conf.Configuration.ServiceConfiguration
import vdi.util.unpackAsTarGZ
import java.io.File

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
    val exitCode = waitFor()
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