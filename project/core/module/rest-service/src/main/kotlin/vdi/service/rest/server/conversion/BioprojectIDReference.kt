package vdi.service.rest.server.conversion

import vdi.service.rest.generated.model.BioprojectIDReferenceImpl
import vdi.model.meta.BioprojectIDReference as InternalType
import vdi.service.rest.generated.model.BioprojectIDReference as RamlType

internal fun BioprojectIDReference(ref: InternalType): RamlType =
  BioprojectIDReferenceImpl().apply {
    id = ref.id
    description = ref.description
  }