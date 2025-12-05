package vdi.service.plugin.script

import vdi.io.plugin.responses.ScriptErrorResponse


internal fun PluginScript.newErrorResponse(
  code:    UByte,
  message: String = "${path.fileName} exited with unexpected status $code"
) = ScriptErrorResponse(path.toString(), code, message)