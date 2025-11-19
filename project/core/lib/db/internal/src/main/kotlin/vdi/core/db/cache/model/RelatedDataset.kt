package vdi.core.db.cache.model

import java.time.OffsetDateTime
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetPublication
import vdi.model.meta.DatasetType

data class RelatedDataset(
  val datasetID: DatasetID,
  val datasetType: DatasetType,
  val name: String,
  val summary: String,
  val created: OffsetDateTime,
  val relationType: RelationType,
  val publication: PublicationRef? = null,
) {
  enum class RelationType { ProgramName, ProjectName, Publication }

  data class PublicationRef(val identifier: String, val type: DatasetPublication.PublicationType)
}
