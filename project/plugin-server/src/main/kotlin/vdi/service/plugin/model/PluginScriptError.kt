package vdi.service.plugin.model

import java.nio.file.Path
import kotlin.io.path.name
import vdi.io.plugin.responses.PluginResponseStatus
import vdi.io.plugin.responses.ScriptErrorResponse
import vdi.service.plugin.script.ExitStatus
import vdi.service.plugin.script.PluginScript

class PluginScriptError(
  val scriptPath: Path,
  val code: UByte = UByte.MAX_VALUE,
  message: String = "${scriptPath.name} exited with unexpected status $code"
)
  : Exception(message)
  , ExpectedError
{
  constructor(script: PluginScript, status: ExitStatus.Info): this(script.path, status.code)

  constructor(script: PluginScript, status: UByte = UByte.MAX_VALUE): this(script.path, status)

  constructor(script: PluginScript, message: String): this(script.path, message = message)

  override val status: PluginResponseStatus
    get() = PluginResponseStatus.ScriptError

  override fun toResponse() =
    ScriptErrorResponse(scriptPath.toString(), code, message!!)
}