package vdi.core.plugin.client.response

import vdi.io.plugin.responses.ValidationResponse

data class SuccessWithWarningsResponse(override val body: ValidationResponse): SuccessResponse, InstallDataResponse {
  fun getWarningsSequence() =
    body.basicWarnings.asSequence() + body.communityWarnings.asSequence()
}