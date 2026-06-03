package vdi.service.rest.server.conversion

import vdi.service.rest.generated.model.DOIReferenceImpl
import vdi.model.meta.DOIReference as InternalType
import vdi.service.rest.generated.model.DOIReference as RamlType

internal fun DOIReference(ref: InternalType): RamlType =
  DOIReferenceImpl().apply {
    doi = ref.doi
    description = ref.description
  }