package vdi.service.rest.server.outputs

import vdi.model.meta.DatasetPublication
import vdi.service.rest.generated.model.DatasetPublicationType

fun DatasetPublicationType(pubType: DatasetPublication.PublicationType): DatasetPublicationType =
  when (pubType) {
    DatasetPublication.PublicationType.PubMed -> DatasetPublicationType.PMID
    DatasetPublication.PublicationType.DOI    -> DatasetPublicationType.DOI
  }
