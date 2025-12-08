package vdi.core.plugin.client.response

import vdi.io.plugin.responses.ValidationResponse

class ValidationErrorResponse(override val body: ValidationResponse)
  : DataErrorResponse
  , ImportResponse
  , InstallDataResponse
  , InstallMetaResponse
{
  fun getWarningsSequence() =
    body.basicWarnings.asSequence() + body.communityWarnings.asSequence()
}