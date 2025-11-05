package vdi.core.plugin.client.response

import vdi.io.plugin.responses.PluginResponseStatus
import vdi.io.plugin.responses.ValidationResponse

data class ValidationResponse(
  override val status: PluginResponseStatus,
  override val body: ValidationResponse,
): PluginResponse {
  fun getWarningsSequence() =
    body.basicValidation.warnings.asSequence() + body.communityValidation.warnings.asSequence()
}