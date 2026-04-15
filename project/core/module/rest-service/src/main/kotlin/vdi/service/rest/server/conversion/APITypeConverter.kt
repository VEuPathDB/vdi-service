package vdi.service.rest.server.conversion

import org.veupathdb.lib.request.validation.ValidationErrors

interface APITypeConverter<R, V> {

  fun cleanup(value: R): R

  fun validate(value: R, jsonPath: String, errors: ValidationErrors)

  fun toExternal(value: V): R

  fun toInternal(value: R): V
}

