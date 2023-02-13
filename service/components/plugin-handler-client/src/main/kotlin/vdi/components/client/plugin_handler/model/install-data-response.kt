package vdi.components.client.plugin_handler.model


interface InstallDataResponse {
  val type: InstallDataResponseType

  val hasMessage: Boolean

  fun getMessage(): String

  val hasWarnings: Boolean

  fun getWarnings(): Collection<String>
}


enum class InstallDataResponseType {
  Success,
  BadRequest,
  ServerError,
}


internal class InstallDataResponseWithWarnings(private val warns: Collection<String>) : InstallDataResponse {

  override val type = InstallDataResponseType.Success

  override val hasWarnings = warns.isNotEmpty()

  override val hasMessage = false

  override fun getWarnings(): Collection<String> = if (hasWarnings) warns else throw noWarningsError()

  override fun getMessage(): String = throw noMessageError()
}


internal class InstallDataResponseWithMessage(
  override val type: InstallDataResponseType,
  private val mess: String
): InstallDataResponse {
  override val hasWarnings = false

  override val hasMessage = mess.isNotBlank()

  override fun getWarnings(): Collection<String> = throw noWarningsError()

  override fun getMessage(): String = if (hasMessage) mess else throw noMessageError()
}


@Suppress("NOTHING_TO_INLINE")
private inline fun noMessageError() =
  IllegalStateException("called getMessage() on an InstallDataResponse that has no message")

@Suppress("NOTHING_TO_INLINE")
private inline fun noWarningsError() =
  IllegalStateException("called getWarnings() on an InstallDataResponse that has no warnings")