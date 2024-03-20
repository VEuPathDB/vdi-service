package vdi.test

import org.mockito.kotlin.*
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.*
import vdi.component.db.cache.CacheDBTransaction
import vdi.component.db.cache.model.*
import vdi.component.db.cache.query.AdminAllDatasetsQuery
import java.time.OffsetDateTime

fun mockCacheDB(
  onSelectDataset: DSGetter<DatasetRecord?> = ::oneParamNull,
  onSelectInstallFiles: DSGetter<List<DatasetFile>> = ::oneParamList,
  onSelectUploadFiles: DSGetter<List<DatasetFile>> = ::oneParamList,
  onSelectAllDatasetCount: (AdminAllDatasetsQuery) -> UInt = { 0u },
  onSelectAllDatasets: (AdminAllDatasetsQuery) -> List<AdminAllDatasetsRow> = ::oneParamList,
  onSelectAdminDetails: DSGetter<AdminDatasetDetailsRecord?> = ::oneParamNull,
  onSelectInstallFileCount: DSGetter<Int> = { 0 },
  onSelectInstallFileSummaries: (List<DatasetID>) -> Map<DatasetID, DatasetFileSummary> = ::oneParamMap,
  onSelectUploadFileCount: DSGetter<Int> = { 0 },
  onSelectUploadFileSummaries: (List<DatasetID>) -> Map<DatasetID, DatasetFileSummary> = ::oneParamMap,
  onSelectDatasetList: (DatasetListQuery) -> List<DatasetRecord> = ::oneParamList,
  onSelectDatasetForUser: (UserID, DatasetID) -> DatasetRecord? = ::twoParamNull,
  onSelectDatasetsForUser: (UserID) -> List<DatasetRecord> = ::oneParamList,
  onSelectNonPrivateDatasets: () -> List<DatasetRecord> = ::noParamList,
  onSelectSharesForDataset: DSGetter<List<DatasetShare>> = ::oneParamList,
  onSelectSharesForDatasets: (List<DatasetID>) -> Map<DatasetID, List<DatasetShare>> = ::oneParamMap,
  onSelectImportControl: DSGetter<DatasetImportStatus?> = ::oneParamNull,
  onSelectImportMessages: DSGetter<List<String>> = ::oneParamList,
  onSelectSyncControl: DSGetter<VDISyncControlRecord?> = ::oneParamNull,
  onSelectDeletedDatasets: () -> List<DeletedDataset> = ::noParamList,
  onSelectOpenSharesFor: (UserID) -> List<DatasetShareListEntry> = ::oneParamList,
  onSelectAcceptedSharesFor: (UserID) -> List<DatasetShareListEntry> = ::oneParamList,
  onSelectRejectedSharesFor: (UserID) -> List<DatasetShareListEntry> = ::oneParamList,
  onSelectAllSharesFor: (UserID) -> List<DatasetShareListEntry> = ::oneParamList,
  onSelectAllSyncControlRecords: () -> Iterable<VDIReconcilerTargetRecord> = ::noParamList,
  onSelectBrokenImports: (BrokenImportListQuery) -> List<BrokenImportRecord> = ::oneParamList,
  onOpenTransaction: () -> CacheDBTransaction = { mockCacheDBTransaction() }
): vdi.component.db.cache.CacheDB =
  mock {
    on { selectDataset(any()) } doAnswer { onSelectDataset(it.getArgument(0)) }
    on { selectInstallFiles(any()) } doAnswer { onSelectInstallFiles(it.getArgument(0)) }
    on { selectUploadFiles(any()) } doAnswer { onSelectUploadFiles(it.getArgument(0)) }
    on { selectAdminAllDatasetCount(any()) } doAnswer { onSelectAllDatasetCount(it.getArgument(0)) }
    on { selectAdminAllDatasets(any()) } doAnswer { onSelectAllDatasets(it.getArgument(0)) }
    on { selectAdminDatasetDetails(any()) } doAnswer { onSelectAdminDetails(it.getArgument(0)) }
    on { selectInstallFileCount(any()) } doAnswer { onSelectInstallFileCount(it.getArgument(0)) }
    on { selectUploadFileCount(any()) } doAnswer { onSelectUploadFileCount(it.getArgument(0)) }
    on { selectInstallFileSummaries(any()) } doAnswer { onSelectInstallFileSummaries(it.getArgument(0)) }
    on { selectUploadFileSummaries(any()) } doAnswer { onSelectUploadFileSummaries(it.getArgument(0)) }
    on { selectDatasetList(any()) } doAnswer { onSelectDatasetList(it.getArgument(0)) }
    on { selectDatasetForUser(any(), any()) } doAnswer { onSelectDatasetForUser(it.getArgument(0), it.getArgument(1)) }
    on { selectDatasetsForUser(any()) } doAnswer { onSelectDatasetsForUser(it.getArgument(0)) }
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
  onInsertUploadFiles: (DatasetID, Iterable<VDIDatasetFileInfo>) -> Unit = ::biConsumer,
  onInsertInstallFiles: (DatasetID, Iterable<VDIDatasetFileInfo>) -> Unit = ::biConsumer,
  onInsertMeta: (DatasetMeta) -> Unit = ::consumer,
  onInsertProjects: (DatasetID, Collection<ProjectID>) -> Unit = ::biConsumer,
  onInsertImportControl: (DatasetID, DatasetImportStatus) -> Unit = ::biConsumer,
  onInsertSyncControl: (VDISyncControlRecord) -> Unit = ::consumer,
  onInsertImportMessages: (DatasetID, String) -> Unit = ::biConsumer,
  onUpdateImportControl: (DatasetID, DatasetImportStatus) -> Unit = ::biConsumer,
  onUpdateMeta: (DatasetMeta) -> Unit = ::consumer,
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
    on { deleteDatasetProjects(any()) } doAnswer { onDeleteProjects(it.getArgument(0)) }
    on { deleteShareOffer(any(), any()) } doAnswer { onDeleteShareOffer(it.getArgument(0), it.getArgument(1)) }
    on { deleteShareReceipt(any(), any()) } doAnswer { onDeleteShareReceipt(it.getArgument(0), it.getArgument(1)) }
    on { deleteDatasetShareOffers(any()) } doAnswer { onDeleteShareOffers(it.getArgument(0)) }
    on { deleteDatasetShareReceipts(any()) } doAnswer { onDeleteShareReceipts(it.getArgument(0)) }
    on { deleteImportControl(any()) } doAnswer { onDeleteImportControl(it.getArgument(0)) }
    on { deleteImportMessages(any()) } doAnswer { onDeleteImportMessages(it.getArgument(0)) }
    on { deleteSyncControl(any()) } doAnswer { onDeleteSyncControl(it.getArgument(0)) }
    on { deleteInstallFiles(any()) } doAnswer { onDeleteInstallFiles(it.getArgument(0)) }
    on { deleteUploadFiles(any()) } doAnswer { onDeleteUploadFiles(it.getArgument(0)) }
    on { tryInsertDataset(any()) } doAnswer { onInsertDataset(it.getArgument(0)) }
    on { tryInsertUploadFiles(any(), any()) } doAnswer { onInsertUploadFiles(it.getArgument(0), it.getArgument(1)) }
    on { tryInsertInstallFiles(any(), any()) } doAnswer { onInsertInstallFiles(it.getArgument(0), it.getArgument(1)) }
    on { tryInsertDatasetMeta(any()) } doAnswer { onInsertMeta(it.getArgument(0)) }
    on { tryInsertDatasetProjects(any(), any()) } doAnswer { onInsertProjects(it.getArgument(0), it.getArgument(1)) }
    on { tryInsertImportControl(any(), any()) } doAnswer { onInsertImportControl(it.getArgument(0), it.getArgument(1)) }
    on { tryInsertSyncControl(any()) } doAnswer { onInsertSyncControl(it.getArgument(0)) }
    on { tryInsertImportMessages(any(), any()) } doAnswer { onInsertImportMessages(it.getArgument(0), it.getArgument(1)) }
    on { updateImportControl(any(), any()) } doAnswer { onUpdateImportControl(it.getArgument(0), it.getArgument(1)) }
    on { updateDatasetMeta(any()) } doAnswer { onUpdateMeta(it.getArgument(0)) }
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

fun mockAdminAllDatasetsRow(
  datasetID: DatasetID? = null,
  ownerID: UserID? = null,
  origin: String? = null,
  created: OffsetDateTime? = null,
  typeName: String? = null,
  typeVersion: String? = null,
  isDeleted: Boolean? = null,
  name: String? = null,
  summary: String? = null,
  description: String? = null,
  sourceURL: String? = null,
  visibility: VDIDatasetVisibility? = null,
  projectIDs: List<ProjectID>? = null,
  importStatus: DatasetImportStatus? = null,
  importMessage: String? = null,
  inserted: OffsetDateTime? = null,
): AdminAllDatasetsRow =
  mock {
    mockDataset(datasetID, typeName, typeVersion, ownerID, isDeleted, created, importStatus, origin, inserted)
    mockMetaAddOn(visibility, name, summary, description, sourceURL)
    projectIDs?.also { on { this.projectIDs } doReturn projectIDs }
    on { this.importMessage } doReturn importMessage
  }

fun mockAdminDatasetDetailsRecord(
  datasetID: DatasetID? = null,
  ownerID: UserID? = null,
  origin: String? = null,
  created: OffsetDateTime? = null,
  inserted: OffsetDateTime? = null,
  typeName: String? = null,
  typeVersion: String? = null,
  isDeleted: Boolean? = null,
  name: String? = null,
  summary: String? = null,
  description: String? = null,
  sourceURL: String? = null,
  visibility: VDIDatasetVisibility? = null,
  projectIDs: List<ProjectID>? = null,
  syncControl: VDISyncControlRecord? = null,
  importStatus: DatasetImportStatus? = null,
  importMessage: String? = null,
  messages: List<String>? = null,
  installFiles: List<String>? = null,
  uploadFiles: List<String>? = null
): AdminDatasetDetailsRecord =
  mock {
    mockDataset(datasetID, typeName, typeVersion, ownerID, isDeleted, created, importStatus, origin, inserted)
    mockMetaAddOn(visibility, name, summary, description, sourceURL)
    projectIDs?.also { on { this.projectIDs } doReturn it }
    on { this.syncControl } doReturn syncControl
    on { this.importMessage } doReturn importMessage
    messages?.also { on { this.messages } doReturn it }
    installFiles?.also { on { this.installFiles } doReturn it }
    uploadFiles?.also { on { this.uploadFiles } doReturn it }
  }

fun mockBrokenImportListQuery(
  userID: UserID? = null,
  before: OffsetDateTime? = null,
  after: OffsetDateTime? = null,
  order: SortOrder? = null,
  sortBy: BrokenImportListQuery.SortField? = null,
  limit: UByte? = null,
  offset: UInt? = null,
): BrokenImportListQuery =
  mock {
    on { this.userID } doReturn userID
    on { this.hasUserID } doReturn (userID != null)

    on { this.before } doReturn before
    on { this.hasBefore } doReturn (before != null)

    on { this.after } doReturn after
    on { this.hasAfter } doReturn (after != null)

    order?.also { on { this.order } doReturn it }
    sortBy?.also { on { this.sortBy } doReturn it }
    limit?.also { on { this.limit } doReturn it }
    offset?.also { on { this.offset } doReturn it }
  }

fun mockBrokenImportRecord(
  datasetID: DatasetID? = null,
  ownerID: UserID? = null,
  typeName: String? = null,
  typeVersion: String? = null,
  projects: List<String>? = null,
  messages: List<String>? = null,
): BrokenImportRecord =
  mock {
    datasetID?.also { on { this.datasetID } doReturn it }
    ownerID?.also { on { this.ownerID } doReturn it }
    typeName?.also { on { this.typeName } doReturn it }
    typeVersion?.also { on { this.typeVersion } doReturn it }
    projects?.also { on { this.projects } doReturn it }
    messages?.also { on { this.messages } doReturn it }
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

fun mockDatasetMeta(
  datasetID: DatasetID? = null,
  visibility: VDIDatasetVisibility? = null,
  name: String? = null,
  summary: String? = null,
  description: String? = null,
  sourceURL: String? = null,
): DatasetMeta =
  mock {
    datasetID?.also { on { this.datasetID } doReturn it }
    mockMetaAddOn(visibility, name, summary, description, sourceURL)
  }

fun mockDatasetFile(fileName: String? = null, fileSize: ULong? = null): DatasetFile =
  mock {
    fileName?.also { on { this.fileName } doReturn it }
    fileSize?.also { on { this.fileSize } doReturn it }
  }

fun mockDatasetFileSummary(count: UInt? = null, size: ULong? = null): DatasetFileSummary =
  mock {
    count?.also { on { this.count } doReturn it }
    size?.also { on { this.size } doReturn it }
  }

fun mockDatasetListQuery(
  userID: Long? = null,
  projectID: String? = null,
  ownership: DatasetOwnershipFilter? = null,
): DatasetListQuery =
  mock {
    userID?.also { on { this.userID } doReturn it }
    on { this.projectID } doReturn projectID
    ownership?.also { on { this.ownership } doReturn it }
  }

fun mockDatasetProjectLinks(
  datasetID: DatasetID? = null,
  projects: List<String>? = null,
): DatasetProjectLinks =
  mock {
    datasetID?.also { on { this.datasetID } doReturn it }
    projects?.also { on { this.projects } doReturn it }
  }

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
  visibility: VDIDatasetVisibility? = null,
  name: String? = null,
  summary: String? = null,
  description: String? = null,
  sourceURL: String? = null,
  projects: Collection<ProjectID>? = null,
): DatasetRecord =
  mock {
    mockDataset(datasetID, typeName, typeVersion, ownerID, isDeleted, created, importStatus, origin, inserted)
    mockMetaAddOn(visibility, name, summary, description, sourceURL)
    projects?.also { on { this.projects } doReturn it }
  }

fun mockDatasetShare(
  recipientID: UserID? = null,
  offerStatus: VDIShareOfferAction? = null,
  receiptStatus: VDIShareReceiptAction? = null,
): DatasetShare =
  mock {
    recipientID?.also { on { this.recipientID } doReturn it }
    on { this.offerStatus } doReturn offerStatus
    on { this.receiptStatus } doReturn receiptStatus
  }

fun mockShareListEntry(
  datasetID: DatasetID? = null,
  ownerID: UserID? = null,
  typeName: String? = null,
  typeVersion: String? = null,
  receiptStatus: VDIShareReceiptAction? = null,
  projects: List<ProjectID>? = null,
): DatasetShareListEntry =
  mock {
    datasetID?.also { on { this.datasetID } doReturn it }
    ownerID?.also { on { this.ownerID } doReturn it }
    typeName?.also { on { this.typeName } doReturn it }
    typeVersion?.also { on { this.typeVersion } doReturn it }
    receiptStatus?.also { on { this.receiptStatus } doReturn it }
    projects?.also { on { this.projects } doReturn it }
  }

fun mockShareOffer(
  datasetID: DatasetID? = null,
  recipientID: UserID? = null,
  action: VDIShareOfferAction? = null,
): DatasetShareOffer =
  mock {
    datasetID?.also { on { this.datasetID } doReturn it }
    recipientID?.also { on { this.recipientID } doReturn it }
    action?.also { on { this.action } doReturn it }
  }

fun mockShareReceipt(
  datasetID: DatasetID? = null,
  recipientID: UserID? = null,
  action: VDIShareReceiptAction? = null,
): DatasetShareReceipt =
  mock {
    datasetID?.also { on { this.datasetID } doReturn it }
    recipientID?.also { on { this.recipientID } doReturn it }
    action?.also { on { this.action } doReturn it }
  }

fun mockDeletedDataset(
  datasetID: DatasetID? = null,
  ownerID: UserID? = null,
  projects: List<ProjectID>? = null,
): DeletedDataset =
  mock {
    datasetID?.also { on { this.datasetID } doReturn it }
    ownerID?.also { on { this.ownerID } doReturn it }
    projects?.also { on { this.projects } doReturn it }
  }

private fun KStubbing<DatasetMeta>.mockMetaAddOn(
  visibility: VDIDatasetVisibility?,
  name: String?,
  summary: String?,
  description: String?,
  sourceURL: String?,
) {
  visibility?.also { on { this.visibility } doReturn it }
  name?.also { on { this.name } doReturn name }
  on { this.summary } doReturn summary
  on { this.description } doReturn description
  on { this.sourceURL } doReturn sourceURL
}

private fun KStubbing<Dataset>.mockDataset(
  datasetID: DatasetID?,
  typeName: String?,
  typeVersion: String?,
  ownerID: UserID?,
  isDeleted: Boolean?,
  created: OffsetDateTime?,
  importStatus: DatasetImportStatus?,
  origin: String?,
  inserted: OffsetDateTime?,
) {
  datasetID?.also { on { this.datasetID } doReturn it }
  typeName?.also { on { this.typeName } doReturn it }
  typeVersion?.also { on { this.typeVersion } doReturn it }
  ownerID?.also { on { this.ownerID } doReturn it }
  isDeleted?.also { on { this.isDeleted } doReturn it }
  created?.also { on { this.created } doReturn it }
  importStatus?.also { on { this.importStatus } doReturn it }
  origin?.also { on { this.origin } doReturn it }
  inserted?.also { on { this.inserted } doReturn it }
}