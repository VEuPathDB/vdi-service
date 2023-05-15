package org.veupathdb.vdi.lib.handler.client.response.ind

internal data class InstallDataValidationFailureResponseImpl(override val warnings: List<String>)
  : InstallDataValidationFailureResponse