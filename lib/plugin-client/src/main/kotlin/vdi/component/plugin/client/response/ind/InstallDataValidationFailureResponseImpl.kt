package vdi.component.plugin.client.response.ind

internal data class InstallDataValidationFailureResponseImpl(override val warnings: Collection<String>)
  : InstallDataValidationFailureResponse