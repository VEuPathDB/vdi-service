package vdi.core.plugin.client.response.ind

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class InstallDataResponseType(
  @get:JsonValue
  val code: Int
) {
  Success(200),
  BadRequest(400),
  ValidationFailure(418),
  MissingDependencies(420),
  UnexpectedError(500),
  ;

  companion object {

    @JvmStatic
    @JsonCreator
    fun fromCode(code: Int) = fromCodeOrNull(code)
      ?: throw IllegalArgumentException("unrecognized InstallDataResponseType code: $code")

    @JvmStatic
    fun fromCodeOrNull(code: Int) = values().firstOrNull { it.code == code }
  }
}
