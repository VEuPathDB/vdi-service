package vdi.test

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import vdi.model.meta.DataType
import vdi.model.meta.DatasetID
import vdi.model.meta.InstallTargetID
import vdi.model.meta.UserID
import vdi.model.meta.*
import org.veupathdb.vdi.core.common.util.CloseableIterator
import java.time.OffsetDateTime
import vdi.core.db.model.ReconcilerTargetRecord
import vdi.core.db.model.SyncControlRecord

internal class ReconcilerStream(it: Iterable<ReconcilerTargetRecord>): CloseableIterator<ReconcilerTargetRecord> {
  private val raw = it.iterator()

  override fun hasNext() = raw.hasNext()

  override fun next() = raw.next()

  override fun close() = Unit
}

fun mockDatasetMeta(
  type: DatasetType? = null,
  projects: Collection<InstallTargetID>? = null,
  visibility: DatasetVisibility? = null,
  owner: UserID? = null,
  name: String? = null,
  summary: String? = null,
  description: String? = null,
  origin: String? = null,
  sourceURL: String? = null,
  dependencies: Collection<DatasetDependency>? = null,
  created: OffsetDateTime? = null,
): DatasetMetadata =
  mock {
    type?.also { on { this.type } doReturn it }
    projects?.also { on { this.installTargets } doReturn it.toSet() }
    visibility?.also { on { this.visibility } doReturn it }
    owner?.also { on { this.owner } doReturn it }
    name?.also { on { this.name } doReturn it }
    summary?.also { on { this.summary } doReturn it }
    on { this.description } doReturn description
    origin?.also { on { this.origin } doReturn it }
    on { this.sourceURL } doReturn sourceURL
    dependencies?.also { on { this.dependencies } doReturn dependencies }
    created?.also { on { this.created } doReturn created }
  }

fun mockDatasetManifest(
  inputFiles: Collection<DatasetFileInfo> = emptyList(),
  dataFiles: Collection<DatasetFileInfo> = emptyList(),
): DatasetManifest =
  mock {
    on { this.inputFiles } doReturn inputFiles
    on { this.dataFiles } doReturn dataFiles
  }

fun mockDatasetFileInfo(
  filename: String? = null,
  size: ULong? = null,
): DatasetFileInfo =
  mock {
    filename?.also { on { this.filename } doReturn it }
    size?.also { on { this.size } doReturn it }
  }

fun mockDatasetDependency(
  identifier: String? = null,
  version: String? = null,
  displayName: String? = null,
): DatasetDependency =
  mock {
    identifier?.also { on { this.identifier } doReturn it }
    version?.also { on { this.version } doReturn it }
    displayName?.also { on { this.displayName } doReturn it }
  }

fun mockDatasetType(
  name: DataType? = null,
  version: String? = null,
): DatasetType =
  mock {
    name?.also { on { this.name } doReturn it }
    version?.also { on { this.version } doReturn it }
  }

fun mockSyncControlRecord(
  datasetID: DatasetID? = null,
  sharesUpdated: OffsetDateTime? = null,
  dataUpdated: OffsetDateTime? = null,
  metaUpdated: OffsetDateTime? = null,
): SyncControlRecord =
  mock {
    datasetID?.also { on { this.datasetID } doReturn it }
    sharesUpdated?.also { on { this.sharesUpdated } doReturn it }
    dataUpdated?.also { on { this.dataUpdated } doReturn it }
    metaUpdated?.also { on { this.metaUpdated } doReturn it }
  }

fun mockReconcilerTargetRecord(
  ownerID: UserID? = null,
  datasetID: DatasetID? = null,
  type: DatasetType? = null,
  sharesUpdated: OffsetDateTime? = null,
  dataUpdated: OffsetDateTime? = null,
  metaUpdated: OffsetDateTime? = null,
): ReconcilerTargetRecord =
  mock {
    ownerID?.also { on { this.ownerID } doReturn it }
    datasetID?.also { on { this.datasetID } doReturn it }
    type?.also { on { this.type } doReturn it }
    sharesUpdated?.also { on { this.sharesUpdated } doReturn it }
    dataUpdated?.also { on { this.dataUpdated } doReturn it }
    metaUpdated?.also { on { this.metaUpdated } doReturn it }
  }
