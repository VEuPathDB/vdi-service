package vdi.core.db.cache.model

import java.time.OffsetDateTime
import vdi.model.data.DatasetID
import vdi.model.data.DatasetPublication
import vdi.model.data.DatasetType

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
