package vdi.service.importing

import io.ktor.server.application.*
import vdi.Const
import vdi.conf.Configuration.ServiceConfiguration
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
    inputStream.lines()
    errorStream.log()

    if (!waitFor(ServiceConfiguration.importScriptMaxSeconds, TimeUnit.SECONDS)) {
      destroyForcibly()
      TODO("500 import script took too long")
    }

    when (exitValue()) {
      Const.ExitCode.IMPORT_SCRIPT_SUCCESS_CODE -> TODO("SUCCESS")
      Const.ExitCode.IMPORT_SCRIPT_FAIL_VALIDATION_CODE -> TODO("Failed validation")
      Const.ExitCode.IMPORT_SCRIPT_FAIL_TRANSFORMATION_CODE -> TODO("Failed transformation")
      else                                              -> TODO("Failed unexpected")
    }
  }
}