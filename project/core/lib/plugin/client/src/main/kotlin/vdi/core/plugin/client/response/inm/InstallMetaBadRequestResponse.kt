package vdi.core.plugin.client.response.inm

sealed interface InstallMetaBadRequestResponse : InstallMetaResponse {
  override val type: InstallMetaResponseType
    get() = InstallMetaResponseType.BadRequest

  val message: String
}
