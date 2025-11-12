package vdi.core.plugin.client.response

import java.io.InputStream
import vdi.io.plugin.responses.ValidationResponse
import vdi.util.zip.zipEntries

sealed interface SuccessResponse: PluginResponse

data object EmptySuccessResponse: SuccessResponse {
  override val body: Unit
    get() = Unit
}

data class StreamSuccessResponse(override val body: InputStream): SuccessResponse {
  fun asZip() =
    body.zipEntries()
}

data class SuccessWithWarningsResponse(override val body: ValidationResponse): SuccessResponse {
  fun getWarningsSequence() =
    body.basicValidation.warnings.asSequence() + body.communityValidation.warnings.asSequence()
}