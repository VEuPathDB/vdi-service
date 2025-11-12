package vdi.core.plugin.client.response

import vdi.io.plugin.responses.MissingDependencyResponse
import vdi.io.plugin.responses.ValidationResponse

sealed interface DataErrorResponse: PluginResponse

data class MissingDependencyResponse(override val body: MissingDependencyResponse): DataErrorResponse

data class ValidationErrorResponse(override val body: ValidationResponse): DataErrorResponse {
  fun getWarningsSequence() =
    body.basicValidation.warnings.asSequence() + body.communityValidation.warnings.asSequence()
}