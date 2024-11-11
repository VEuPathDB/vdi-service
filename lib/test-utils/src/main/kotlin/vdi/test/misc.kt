package vdi.test

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.*
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import java.time.OffsetDateTime

internal class ReconcilerStream(it: Iterable<VDIReconcilerTargetRecord>): CloseableIterator<VDIReconcilerTargetRecord> {
  private val raw = it.iterator()

  override fun hasNext() = raw.hasNext()

  override fun next() = raw.next()

  override fun close() = Unit
}

fun mockDatasetMeta(
  type: VDIDatasetType? = null,
  projects: Collection<ProjectID>? = null,
  visibility: VDIDatasetVisibility? = null,
  owner: UserID? = null,
  name: String? = null,
  summary: String? = null,
  description: String? = null,
  origin: String? = null,
  sourceURL: String? = null,
  dependencies: Collection<VDIDatasetDependency>? = null,
  created: OffsetDateTime? = null,
): VDIDatasetMeta =
  mock {
    type?.also { on { this.type } doReturn it }
    projects?.also { on { this.projects } doReturn it.toSet() }
    visibility?.also { on { this.visibility } doReturn it }
    owner?.also { on { this.owner } doReturn it }
    name?.also { on { this.name } doReturn it }
    on { this.summary } doReturn summary
    on { this.description } doReturn description
    origin?.also { on { this.origin } doReturn it }
    on { this.sourceURL } doReturn sourceURL
    dependencies?.also { on { this.dependencies } doReturn dependencies }
    created?.also { on { this.created } doReturn created }
  }

fun mockDatasetManifest(
  inputFiles: Collection<VDIDatasetFileInfo> = emptyList(),
  dataFiles: Collection<VDIDatasetFileInfo> = emptyList(),
): VDIDatasetManifest =
  mock {
    on { this.inputFiles } doReturn inputFiles
    on { this.dataFiles } doReturn dataFiles
  }

fun mockDatasetFileInfo(
  filename: String? = null,
  fileSize: Long? = null,
): VDIDatasetFileInfo =
  mock {
    filename?.also { on { this.filename } doReturn it }
    fileSize?.also { on { this.fileSize } doReturn it }
  }

fun mockDatasetDependency(
  identifier: String? = null,
  version: String? = null,
  displayName: String? = null,
): VDIDatasetDependency =
  mock {
    identifier?.also { on { this.identifier } doReturn it }
    version?.also { on { this.version } doReturn it }
    displayName?.also { on { this.displayName } doReturn it }
  }

fun mockDatasetType(
  name: DataType? = null,
  version: String? = null,
): VDIDatasetType =
  mock {
    name?.also { on { this.name } doReturn it }
    version?.also { on { this.version } doReturn it }
  }

fun mockSyncControlRecord(
  datasetID: DatasetID? = null,
  sharesUpdated: OffsetDateTime? = null,
  dataUpdated: OffsetDateTime? = null,
  metaUpdated: OffsetDateTime? = null,
): VDISyncControlRecord =
  mock {
    datasetID?.also { on { this.datasetID } doReturn it }
    sharesUpdated?.also { on { this.sharesUpdated } doReturn it }
    dataUpdated?.also { on { this.dataUpdated } doReturn it }
    metaUpdated?.also { on { this.metaUpdated } doReturn it }
  }

fun mockReconcilerTargetRecord(
  ownerID: UserID? = null,
  datasetID: DatasetID? = null,
  type: VDIDatasetType? = null,
  sharesUpdated: OffsetDateTime? = null,
  dataUpdated: OffsetDateTime? = null,
  metaUpdated: OffsetDateTime? = null,
  comparableID: String? = if (ownerID != null && datasetID != null) "$ownerID/$datasetID" else null,
): VDIReconcilerTargetRecord =
  mock {
    ownerID?.also { on { this.ownerID } doReturn it }
    datasetID?.also { on { this.datasetID } doReturn it }
    type?.also { on { this.type } doReturn it }
    sharesUpdated?.also { on { this.sharesUpdated } doReturn it }
    dataUpdated?.also { on { this.dataUpdated } doReturn it }
    metaUpdated?.also { on { this.metaUpdated } doReturn it }
    comparableID?.also { on { this.getComparableID() } doReturn it }
  }
