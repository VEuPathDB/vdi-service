package vdi.service.plugin.model

import vdi.io.plugin.responses.PluginResponseStatus
import vdi.io.plugin.responses.ValidationResponse

class ValidationError(val errors: ValidationResponse): RuntimeException(), ExpectedError {
  override val status: PluginResponseStatus
    get() = PluginResponseStatus.ValidationError

  override fun toResponse() = errors
}