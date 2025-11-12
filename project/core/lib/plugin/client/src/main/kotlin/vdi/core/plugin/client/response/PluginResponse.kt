package vdi.core.plugin.client.response

import vdi.io.plugin.responses.PluginResponseStatus

sealed interface PluginResponse: AutoCloseable {
  val status: PluginResponseStatus
    get() = when (this) {
      is SuccessResponse           -> PluginResponseStatus.Success
      is ValidationErrorResponse   -> PluginResponseStatus.ValidationError
      is MissingDependencyResponse -> PluginResponseStatus.MissingDependencyError
      is ScriptErrorResponse       -> PluginResponseStatus.ScriptError
      is ServerErrorResponse       -> PluginResponseStatus.ServerError
    }

  val body: Any

  override fun close() {
    (body as? AutoCloseable)?.close()
  }
}

