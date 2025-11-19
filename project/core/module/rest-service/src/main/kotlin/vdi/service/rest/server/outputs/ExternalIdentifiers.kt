package vdi.service.rest.server.outputs

import vdi.model.meta.BioprojectIDReference
import vdi.model.meta.DOIReference
import vdi.model.meta.DatasetHyperlink
import vdi.model.meta.ExternalDatasetIdentifiers
import vdi.service.rest.generated.model.BioprojectIDReferenceImpl
import vdi.service.rest.generated.model.DOIReferenceImpl
import vdi.service.rest.generated.model.DatasetHyperlinkImpl
import vdi.service.rest.generated.model.ExternalIdentifiersImpl
import vdi.service.rest.generated.model.BioprojectIDReference as APIBioRef
import vdi.service.rest.generated.model.DOIReference as APIDOI
import vdi.service.rest.generated.model.DatasetHyperlink as APIHyperlink
import vdi.service.rest.generated.model.ExternalIdentifiers as APIIdentifiers

internal fun ExternalIdentifiers(identifiers: ExternalDatasetIdentifiers): APIIdentifiers =
  ExternalIdentifiersImpl().also {
    it.dois          = identifiers.dois.map(::DOIReference)
    it.hyperlinks    = identifiers.hyperlinks.map(::DatasetHyperlink)
    it.bioprojectIds = identifiers.bioprojectIDs.map(::BioprojectIDReference)
  }

private fun DOIReference(ref: DOIReference): APIDOI =
  DOIReferenceImpl().also {
    it.doi = ref.doi
    it.description = ref.description
  }

private fun DatasetHyperlink(link: DatasetHyperlink): APIHyperlink =
  DatasetHyperlinkImpl().also {
    it.url = link.url.toString()
    it.description = link.description
  }

private fun BioprojectIDReference(ref: BioprojectIDReference): APIBioRef =
  BioprojectIDReferenceImpl().also {
    it.id = ref.id
    it.description = ref.description
  }