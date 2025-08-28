@file:JvmName("RelatedDatasetInfoOutputAdaptor")
package vdi.service.rest.server.outputs

import vdi.core.db.cache.model.RelatedDataset
import vdi.service.rest.generated.model.*

fun RelatedDatasetInfo(
  other: RelatedDataset,
): RelatedDatasetInfo =
  RelatedDatasetInfoImpl().apply {
    datasetId = other.datasetID.toString()
    type      = DatasetTypeOutput(other.datasetType)
    name      = other.name
    summary   = other.summary
    created   = other.created
    relatedBy = ImplicitRelation(other)
  }

private fun ImplicitRelation(other: RelatedDataset): ImplicitRelation =
  when (other.relationType) {
    RelatedDataset.RelationType.ProgramName -> RelationByProgramNameImpl()
    RelatedDataset.RelationType.ProjectName -> RelationByProjectNameImpl()
    RelatedDataset.RelationType.Publication -> RelationByPublicationImpl().apply {
      identifier = other.publication!!.identifier
      type       = DatasetPublicationType(other.publication!!.type)
    }
  }
