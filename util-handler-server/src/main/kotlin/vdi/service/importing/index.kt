package vdi.service.importing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import vdi.Const
import vdi.conf.Configuration.ServiceConfiguration
import vdi.server.model.makeSimpleErrorJSONString
import vdi.util.unpackAsTarGZ
import java.io.File
import java.util.concurrent.TimeUnit

suspend fun ApplicationCall.processImport(vdiID: String, parentDir: File, inputArchive: File) {

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
//    inputStream.lines()
//    errorStream.log()

    if (!waitFor(ServiceConfiguration.importScriptMaxSeconds, TimeUnit.SECONDS)) {
      destroyForcibly()
      respond(HttpStatusCode.InternalServerError, makeSimpleErrorJSONString("import script was forcibly killed due to timeout"))
      return
    }

    when (exitValue()) {
      Const.ExitCode.ImportScriptSuccess                    -> TODO("SUCCESS")
      Const.ExitCode.ImportScriptValidationFailure     -> TODO("Failed validation")
      Const.ExitCode.ImportScriptTransformationFailure -> TODO("Failed transformation")
      else                                             -> TODO("Failed unexpected")
    }
  }
}