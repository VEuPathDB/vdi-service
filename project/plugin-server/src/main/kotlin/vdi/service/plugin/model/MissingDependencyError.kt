package vdi.service.plugin.model

import vdi.io.plugin.responses.MissingDependencyResponse
import vdi.io.plugin.responses.PluginResponseStatus

class MissingDependencyError(val warnings: Collection<String>): RuntimeException(), ExpectedError {
  override val status: PluginResponseStatus
    get() = PluginResponseStatus.MissingDependencyError

  override fun toResponse() = MissingDependencyResponse(warnings)
}