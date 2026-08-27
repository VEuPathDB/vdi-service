package vdi.service.rest.server.inputs

import vdi.service.rest.generated.model.OptionalBooleanPatch
import vdi.service.rest.generated.model.OptionalBooleanPatchImpl

fun OptionalBooleanPatch(value: Boolean?): OptionalBooleanPatch =
  OptionalBooleanPatchImpl().also { it.value = value }
