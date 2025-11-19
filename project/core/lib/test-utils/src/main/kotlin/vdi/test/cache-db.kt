package vdi.test

import org.mockito.kotlin.KStubbing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.OffsetDateTime
import vdi.core.db.cache.model.CoreDatasetMeta
import vdi.core.db.cache.model.Dataset
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.DatasetRecord
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetVisibility
import vdi.model.meta.InstallTargetID
import vdi.model.meta.UserID

fun mockDataset(
  datasetID: DatasetID? = null,
  typeName: String? = null,
  typeVersion: String? = null,
  ownerID: UserID? = null,
  isDeleted: Boolean? = null,
  created: OffsetDateTime? = null,
  importStatus: DatasetImportStatus? = null,
  origin: String? = null,
  inserted: OffsetDateTime? = null,
): Dataset =
  mock { mockDataset(datasetID, typeName, typeVersion, ownerID, isDeleted, created, importStatus, origin, inserted) }

fun mockCacheDatasetRecord(
  datasetID: DatasetID? = null,
  typeName: String? = null,
  typeVersion: String? = null,
  ownerID: UserID? = null,
  isDeleted: Boolean? = null,
  created: OffsetDateTime? = null,
  importStatus: DatasetImportStatus? = null,
  origin: String? = null,
  inserted: OffsetDateTime? = null,
  visibility: DatasetVisibility? = null,
  name: String? = null,
  summary: String? = null,
  description: String? = null,
  sourceURL: String? = null,
  projects: List<InstallTargetID>? = null,
): DatasetRecord =
  mock {
    mockDataset(datasetID, typeName, typeVersion, ownerID, isDeleted, created, importStatus, origin, inserted)
    mockMetaAddOn(visibility, name, summary, description, sourceURL)
    projects?.also { on { this.projects } doReturn it }
  }

private fun KStubbing<CoreDatasetMeta>.mockMetaAddOn(
  visibility: DatasetVisibility?,
  name: String?,
  summary: String?,
  description: String?,
  sourceURL: String?,
) {
  visibility?.also { on { this.visibility } doReturn it }
  name?.also { on { this.name } doReturn name }
  summary?.also { on { this.summary } doReturn summary }
  on { this.description } doReturn description
  on { this.sourceURL } doReturn sourceURL
}
