package vdi.test

import org.mockito.kotlin.*
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.model.data.UserID
import vdi.model.data.DatasetFileInfo
import vdi.model.data.DatasetMetadata
import vdi.model.data.DatasetVisibility
import java.time.OffsetDateTime
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.CacheDBTransaction
import vdi.core.db.cache.model.*
import vdi.core.db.cache.query.AdminAllDatasetsQuery
import vdi.core.db.cache.query.DatasetListQuery
import vdi.core.db.model.ReconcilerTargetRecord
import vdi.core.db.model.SyncControlRecord

fun mockCacheDB(
  onSelectDataset: DSGetter<DatasetRecord?> = ::oneParamNull,
  onSelectInstallFiles: DSGetter<List<DatasetFileInfo>> = ::oneParamList,
  onSelectUploadFiles: DSGetter<List<DatasetFileInfo>> = ::oneParamList,
  onSelectAllDatasetCount: (AdminAllDatasetsQuery) -> UInt = { 0u },
  onSelectAllDatasets: (AdminAllDatasetsQuery) -> List<AdminAllDatasetsRow> = ::oneParamList,
  onSelectAdminDetails: DSGetter<AdminDatasetDetailsRecord?> = ::oneParamNull,
  onSelectInstallFileCount: DSGetter<Int> = { 0 },
  onSelectUploadFileCount: DSGetter<Int> = { 0 },
  onSelectUploadFileSummaries: (List<DatasetID>) -> Map<DatasetID, DatasetFileSummary> = ::oneParamMap,
  onSelectDatasetList: (DatasetListQuery) -> List<DatasetRecord> = ::oneParamList,
  onSelectDatasetForUser: (UserID, DatasetID) -> DatasetRecord? = ::twoParamNull,
  onSelectDatasetsForUser: (UserID) -> List<DatasetID> = ::oneParamList,
  onSelectNonPrivateDatasets: () -> List<DatasetRecord> = ::noParamList,
  onSelectSharesForDataset: DSGetter<List<DatasetShare>> = ::oneParamList,
  onSelectSharesForDatasets: (List<DatasetID>) -> Map<DatasetID, List<DatasetShare>> = ::oneParamMap,
  onSelectImportControl: DSGetter<DatasetImportStatus?> = ::oneParamNull,
  onSelectImportMessages: DSGetter<List<String>> = ::oneParamList,
  onSelectSyncControl: DSGetter<SyncControlRecord?> = ::oneParamNull,
  onSelectDeletedDatasets: () -> List<DeletedDataset> = ::noParamList,
  onSelectOpenSharesFor: (UserID) -> List<DatasetShareListEntry> = ::oneParamList,
  onSelectAcceptedSharesFor: (UserID) -> List<DatasetShareListEntry> = ::oneParamList,
  onSelectRejectedSharesFor: (UserID) -> List<DatasetShareListEntry> = ::oneParamList,
  onSelectAllSharesFor: (UserID) -> List<DatasetShareListEntry> = ::oneParamList,
  onSelectAllSyncControlRecords: () -> Iterable<ReconcilerTargetRecord> = ::noParamList,
  onSelectBrokenImports: (BrokenImportListQuery) -> List<BrokenImportRecord> = ::oneParamList,
  onOpenTransaction: () -> CacheDBTransaction = { mockCacheDBTransaction() }
): CacheDB =
  mock {
    on { selectDataset(any()) } doAnswer { onSelectDataset(it.getArgument(0)) }
    on { selectInstallFiles(any()) } doAnswer { onSelectInstallFiles(it.getArgument(0)) }
    on { selectUploadFiles(any()) } doAnswer { onSelectUploadFiles(it.getArgument(0)) }
    on { selectAdminAllDatasetCount(any()) } doAnswer { onSelectAllDatasetCount(it.getArgument(0)) }
    on { selectAdminAllDatasets(any()) } doAnswer { onSelectAllDatasets(it.getArgument(0)) }
    on { selectAdminDatasetDetails(any()) } doAnswer { onSelectAdminDetails(it.getArgument(0)) }
    on { selectInstallFileCount(any()) } doAnswer { onSelectInstallFileCount(it.getArgument(0)) }
    on { selectUploadFileCount(any()) } doAnswer { onSelectUploadFileCount(it.getArgument(0)) }
    on { selectUploadFileSummaries(any()) } doAnswer { onSelectUploadFileSummaries(it.getArgument(0)) }
    on { selectDatasetList(any()) } doAnswer { onSelectDatasetList(it.getArgument(0)) }
    on { selectDatasetForUser(any(), any()) } doAnswer { onSelectDatasetForUser(it.getArgument(0), it.getArgument(1)) }
    on { selectUndeletedDatasetIDsForUser(any()) } doAnswer { onSelectDatasetsForUser(it.getArgument(0)) }
    on { selectNonPrivateDatasets() } doAnswer { onSelectNonPrivateDatasets() }
    on { selectSharesForDataset(any()) } doAnswer { onSelectSharesForDataset(it.getArgument(0)) }
    on { selectSharesForDatasets(any()) } doAnswer { onSelectSharesForDatasets(it.getArgument(0)) }
    on { selectImportControl(any()) } doAnswer { onSelectImportControl(it.getArgument(0)) }
    on { selectImportMessages(any()) } doAnswer { onSelectImportMessages(it.getArgument(0)) }
    on { selectDeletedDatasets() } doAnswer { onSelectDeletedDatasets() }
    on { selectOpenSharesForUser(any()) } doAnswer { onSelectOpenSharesFor(it.getArgument(0)) }
    on { selectAcceptedSharesForUser(any()) } doAnswer { onSelectAcceptedSharesFor(it.getArgument(0)) }
    on { selectRejectedSharesForUser(any()) } doAnswer { onSelectRejectedSharesFor(it.getArgument(0)) }
    on { selectAllSharesForUser(any()) } doAnswer { onSelectAllSharesFor(it.getArgument(0)) }
    on { selectAllSyncControlRecords() } doAnswer { ReconcilerStream(onSelectAllSyncControlRecords()) }
    on { selectBrokenDatasetImports(any()) } doAnswer { onSelectBrokenImports(it.getArgument(0)) }
    on { selectSyncControl(any()) } doAnswer { onSelectSyncControl(it.getArgument(0)) }
    on { openTransaction() } doAnswer { onOpenTransaction() }
  }

fun mockCacheDBTransaction(
  onDeleteDataset: DSConsumer = ::consumer,
  onDeleteMeta: DSConsumer = ::consumer,
  onDeleteProjects: DSConsumer = ::consumer,
  onDeleteShareOffer: (DatasetID, UserID) -> Unit = ::biConsumer,
  onDeleteShareOffers: DSConsumer = ::consumer,
  onDeleteShareReceipt: (DatasetID, UserID) -> Unit = ::biConsumer,
  onDeleteShareReceipts: DSConsumer = ::consumer,
  onDeleteImportControl: DSConsumer = ::consumer,
  onDeleteImportMessages: DSConsumer = ::consumer,
  onDeleteSyncControl: DSConsumer = ::consumer,
  onDeleteInstallFiles: DSConsumer = ::consumer,
  onDeleteUploadFiles: DSConsumer = ::consumer,
  onInsertDataset: (Dataset) -> Unit = ::consumer,
  onInsertUploadFiles: (DatasetID, Iterable<DatasetFileInfo>) -> Unit = ::biConsumer,
  onInsertInstallFiles: (DatasetID, Iterable<DatasetFileInfo>) -> Unit = ::biConsumer,
  onInsertMeta: (DatasetID, DatasetMetadata) -> Unit = ::biConsumer,
  onInsertProjects: (DatasetID, Collection<InstallTargetID>) -> Unit = ::biConsumer,
  onInsertImportControl: (DatasetID, DatasetImportStatus) -> Unit = ::biConsumer,
  onInsertSyncControl: (SyncControlRecord) -> Unit = ::consumer,
  onInsertImportMessages: (DatasetID, String) -> Unit = ::biConsumer,
  onUpdateImportControl: (DatasetID, DatasetImportStatus) -> Unit = ::biConsumer,
  onUpdateMeta: (DatasetID, DatasetMetadata) -> Unit = ::biConsumer,
  onUpdateMetaSync: DSSync = ::biConsumer,
  onUpdateDataSync: DSSync = ::biConsumer,
  onUpdateShareSync: DSSync = ::biConsumer,
  onUpsertShareOffer: (DatasetShareOffer) -> Unit = ::consumer,
  onUpsertShareReceipt: (DatasetShareReceipt) -> Unit = ::consumer,
  onUpsertImportControl: (DatasetID, DatasetImportStatus) -> Unit = ::biConsumer,
  onUpsertImportMessage: (DatasetID, String) -> Unit = ::biConsumer,
  onUpdateDatasetDeleted: (DatasetID, Boolean) -> Unit = ::biConsumer,
  onCommit: Runnable = ::runnable,
  onRollback: Runnable = ::runnable,
  onClose: Runnable = ::runnable,
): CacheDBTransaction =
  mock {
    on { deleteDataset(any()) } doAnswer { onDeleteDataset(it.getArgument(0)) }
    on { deleteDatasetMetadata(any()) } doAnswer { onDeleteMeta(it.getArgument(0)) }
    on { deleteInstallTargetLinks(any()) } doAnswer { onDeleteProjects(it.getArgument(0)) }
    on { deleteShareOffer(any(), any()) } doAnswer { onDeleteShareOffer(it.getArgument(0), it.getArgument(1)) }
    on { deleteShareReceipt(any(), any()) } doAnswer { onDeleteShareReceipt(it.getArgument(0), it.getArgument(1)) }
    on { deleteShareOffers(any()) } doAnswer { onDeleteShareOffers(it.getArgument(0)) }
    on { deleteShareReceipts(any()) } doAnswer { onDeleteShareReceipts(it.getArgument(0)) }
    on { deleteImportControl(any()) } doAnswer { onDeleteImportControl(it.getArgument(0)) }
    on { deleteImportMessages(any()) } doAnswer { onDeleteImportMessages(it.getArgument(0)) }
    on { deleteSyncControl(any()) } doAnswer { onDeleteSyncControl(it.getArgument(0)) }
    on { deleteInstallFiles(any()) } doAnswer { onDeleteInstallFiles(it.getArgument(0)) }
    on { deleteUploadFiles(any()) } doAnswer { onDeleteUploadFiles(it.getArgument(0)) }
    on { tryInsertDataset(any()) } doAnswer { onInsertDataset(it.getArgument(0)) }
    on { tryInsertUploadFiles(any(), any()) } doAnswer { onInsertUploadFiles(it.getArgument(0), it.getArgument(1)) }
    on { tryInsertInstallFiles(any(), any()) } doAnswer { onInsertInstallFiles(it.getArgument(0), it.getArgument(1)) }
    on { tryInsertDatasetMeta(any(), any()) } doAnswer { onInsertMeta(it.getArgument(0), it.getArgument(1)) }
    on { tryInsertDatasetProjects(any(), any()) } doAnswer { onInsertProjects(it.getArgument(0), it.getArgument(1)) }
    on { tryInsertImportControl(any(), any()) } doAnswer { onInsertImportControl(it.getArgument(0), it.getArgument(1)) }
    on { tryInsertSyncControl(any()) } doAnswer { onInsertSyncControl(it.getArgument(0)) }
    on { tryInsertImportMessages(any(), any()) } doAnswer { onInsertImportMessages(it.getArgument(0), it.getArgument(1)) }
    on { updateImportControl(any(), any()) } doAnswer { onUpdateImportControl(it.getArgument(0), it.getArgument(1)) }
    on { updateDatasetMeta(any(), any()) } doAnswer { onUpdateMeta(it.getArgument(0), it.getArgument(1)) }
    on { updateMetaSyncControl(any(), any()) } doAnswer { onUpdateMetaSync(it.getArgument(0), it.getArgument(1)) }
    on { updateDataSyncControl(any(), any()) } doAnswer { onUpdateDataSync(it.getArgument(0), it.getArgument(1)) }
    on { updateShareSyncControl(any(), any()) } doAnswer { onUpdateShareSync(it.getArgument(0), it.getArgument(1)) }
    on { upsertDatasetShareOffer(any()) } doAnswer { onUpsertShareOffer(it.getArgument(0)) }
    on { upsertDatasetShareReceipt(any()) } doAnswer { onUpsertShareReceipt(it.getArgument(0)) }
    on { upsertImportControl(any(), any()) } doAnswer { onUpsertImportControl(it.getArgument(0), it.getArgument(1)) }
    on { upsertImportMessages(any(), any()) } doAnswer { onUpsertImportMessage(it.getArgument(0), it.getArgument(1)) }
    on { updateDatasetDeleted(any(), any()) } doAnswer { onUpdateDatasetDeleted(it.getArgument(0), it.getArgument(1)) }
    on { rollback() } doAnswer { onRollback() }
    on { commit() } doAnswer { onCommit() }
    on { close() } doAnswer { onClose() }
  }

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
  shortName: String? = null,
  shortAttribution: String? = null,
  summary: String? = null,
  description: String? = null,
  sourceURL: String? = null,
  projects: List<InstallTargetID>? = null,
): DatasetRecord =
  mock {
    mockDataset(datasetID, typeName, typeVersion, ownerID, isDeleted, created, importStatus, origin, inserted)
    mockMetaAddOn(visibility, name, shortName, shortAttribution, summary, description, sourceURL)
    projects?.also { on { this.projects } doReturn it }
  }

private fun KStubbing<CoreDatasetMeta>.mockMetaAddOn(
  visibility: DatasetVisibility?,
  name: String?,
  shortName: String?,
  shortAttribution: String?,
  summary: String?,
  description: String?,
  sourceURL: String?,
) {
  visibility?.also { on { this.visibility } doReturn it }
  name?.also { on { this.name } doReturn name }
  on { this.shortName } doReturn shortName
  on { this.shortAttribution } doReturn shortAttribution
  on { this.summary } doReturn summary
  on { this.description } doReturn description
  on { this.sourceURL } doReturn sourceURL
}
