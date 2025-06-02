package vdi.core.plugin.client.response.ind

internal data class InstallDataValidationFailureResponseImpl(override val warnings: Collection<String>)
  : InstallDataValidationFailureResponse
