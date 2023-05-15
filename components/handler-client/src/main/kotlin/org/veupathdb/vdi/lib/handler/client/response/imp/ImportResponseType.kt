package org.veupathdb.vdi.lib.handler.client.response.imp

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class ImportResponseType(
  @get:JsonValue
  val code: Int
) {
  Success(200),
  BadRequest(400),
  ValidationError(418),
  UnhandledError(500),
  ;

  companion object {

    @JvmStatic
    @JsonCreator
    fun fromCode(code: Int) = fromCodeOrNull(code)
      ?: throw IllegalArgumentException("unrecognized ImportResponseType code: $code")

    @JvmStatic
    fun fromCodeOrNull(code: Int) = values().find { it.code == code }
  }
}