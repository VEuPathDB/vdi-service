package org.veupathdb.vdi.lib.handler.client.response.uni

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class UninstallResponseType(
  @get:JsonValue
  val code: Int
) {
  Success(204),
  BadRequest(400),
  UnexpectedError(500),
  ;

  companion object {

    @JvmStatic
    @JsonCreator
    fun fromCode(code: Int) = fromCodeOrNull(code)
      ?: throw IllegalArgumentException("unrecognized UninstallResponseType code: $code")

    @JvmStatic
    fun fromCodeOrNull(code: Int) = values().firstOrNull { it.code == code }
  }
}