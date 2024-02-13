package vdi.test

import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.AppDBAccessor
import org.veupathdb.vdi.lib.db.app.AppDBTransaction
import org.veupathdb.vdi.lib.db.app.model.*
import java.time.OffsetDateTime

fun mockAppDB(
  accessor: (ProjectID) -> AppDBAccessor? = ::oneParamNull,
  transaction: (ProjectID) -> AppDBTransaction? = ::oneParamNull,
  bulkStatuses: (Map<ProjectID, Collection<DatasetID>>) -> Map<DatasetID, Map<ProjectID, InstallStatuses>> = ::oneParamMap,
  datasetStatuses: (DatasetID, Collection<ProjectID>) -> Map<ProjectID, InstallStatuses> = ::twoParamMap,
): AppDB =
  mock {
    on { getDatasetStatuses(any()) } doAnswer { bulkStatuses(it.getArgument(0)) }
    on { getDatasetStatuses(any(), any()) } doAnswer { datasetStatuses(it.getArgument(0), it.getArgument(1)) }
    on { this.accessor(any()) } doAnswer { accessor(it.getArgument(0)) }
    on { this.transaction(any()) } doAnswer { transaction(it.getArgument(0)) }
  }

fun mockAppDBAccessor(
  dataset: DSGetter<DatasetRecord?> = ::oneParamNull,
  installMessage: (DatasetID, InstallType) -> DatasetInstallMessage? = ::twoParamNull,
  installMessages: DSGetter<List<DatasetInstallMessage>> = ::oneParamList,
  syncControl: DSGetter<VDISyncControlRecord?> = ::oneParamNull,
  visibilityRecords: DSGetter<List<DatasetVisibilityRecord>> = ::oneParamList,
  projectLinks: DSGetter<List<DatasetProjectLinkRecord>> = ::oneParamList,
  reconcilerRecords: () -> Iterable<VDIReconcilerTargetRecord> = ::noParamList,
  byInstallStatus: (InstallType, InstallStatus) -> List<DatasetRecord> = ::twoParamList,
  visibilityExists: (DatasetID, UserID) -> Boolean = { _, _ -> false },
  projectLinkExists: (DatasetID, ProjectID) -> Boolean = { _, _ -> false },
): AppDBAccessor =
  mock {
    on { selectDataset(any()) } doAnswer { dataset(it.getArgument(0)) }
    on { selectDatasetInstallMessage(any(), any()) } doAnswer { installMessage(it.getArgument(0), it.getArgument(1)) }
    on { selectDatasetInstallMessages(any()) } doAnswer { installMessages(it.getArgument(0)) }
    on { selectDatasetSyncControlRecord(any()) } doAnswer { syncControl(it.getArgument(0)) }
    on { selectDatasetVisibilityRecords(any()) } doAnswer { visibilityRecords(it.getArgument(0)) }
    on { selectDatasetProjectLinks(any()) } doAnswer { projectLinks(it.getArgument(0)) }
    on { streamAllSyncControlRecords() } doAnswer { ReconcilerStream(reconcilerRecords()) }
    on { selectDatasetsByInstallStatus(any(), any()) } doAnswer { byInstallStatus(it.getArgument(0), it.getArgument(1)) }
    on { testDatasetVisibilityExists(any(), any()) } doAnswer { visibilityExists(it.getArgument(0), it.getArgument(1)) }
    on { testDatasetProjectLinkExists(any(), any()) } doAnswer { projectLinkExists(it.getArgument(0), it.getArgument(1)) }
  }

fun mockAppDBTransaction(
  dataset: DSGetter<DatasetRecord?> = ::oneParamNull,
  installMessage: (DatasetID, InstallType) -> DatasetInstallMessage? = ::twoParamNull,
  installMessages: DSGetter<List<DatasetInstallMessage>> = ::oneParamList,
  syncControl: DSGetter<VDISyncControlRecord?> = ::oneParamNull,
  visibilityRecords: DSGetter<List<DatasetVisibilityRecord>> = ::oneParamList,
  projectLinks: DSGetter<List<DatasetProjectLinkRecord>> = ::oneParamList,
  reconcilerRecords: () -> Iterable<VDIReconcilerTargetRecord> = { emptyList() },
  byInstallStatus: (InstallType, InstallStatus) -> List<DatasetRecord> = ::twoParamList,
  visibilityExists: (DatasetID, UserID) -> Boolean = { _, _ -> false },
  projectLinkExists: (DatasetID, ProjectID) -> Boolean = { _, _ -> false },
  onDeleteDataset: DSConsumer = ::consumer,
  onDeleteSyncControl: DSConsumer = ::consumer,
  onDeleteInstallMessages: DSConsumer = ::consumer,
  onDeleteInstallMessage: (DatasetID, InstallType) -> Unit = ::biConsumer,
  onDeleteVisibilities: DSConsumer = ::consumer,
  onDeleteVisibility: (DatasetID, UserID) -> Unit = ::biConsumer,
  onDeleteProjectLinks: DSConsumer = ::consumer,
  onDeleteProjectLink: (DatasetID, ProjectID) -> Unit = ::biConsumer,
  onDeleteMeta: DSConsumer = ::consumer,
  onInsertDataset: (DatasetRecord) -> Unit = ::consumer,
  onInsertInstallMessage: (DatasetInstallMessage) -> Unit = ::consumer,
  onInsertProjectLink: (DatasetID, ProjectID) -> Unit = ::biConsumer,
  onInsertProjectLinks: (DatasetID, Iterable<ProjectID>) -> Unit = ::biConsumer,
  onInsertVisibility: (DatasetID, UserID) -> Unit = ::biConsumer,
  onInsertSyncControl: (VDISyncControlRecord) -> Unit = ::consumer,
  onInsertMeta: (DatasetID, String, String?) -> Unit = ::triConsumer,
  onUpdateDataset: (DatasetRecord) -> Unit = ::consumer,
  onUpdateDeleteFlag: (DatasetID, DeleteFlag) -> Unit = ::biConsumer,
  onUpdateSyncControlData: DSSync = ::biConsumer,
  onUpdateSyncControlMeta: DSSync = ::biConsumer,
  onUpdateSyncControlShares: DSSync = ::biConsumer,
  onUpdateInstallMessage: (DatasetInstallMessage) -> Unit = ::consumer,
  onUpdateMeta: (DatasetID, String, String?) -> Unit = ::triConsumer,
  onUpsertMeta: (DatasetID, String, String?) -> Unit = ::triConsumer,
  onRollback: Runnable = ::runnable,
  onCommit: Runnable = ::runnable,
): AppDBTransaction =
  mock {
    on { selectDataset(any()) } doAnswer { dataset(it.getArgument(0)) }
    on { selectDatasetInstallMessage(any(), any()) } doAnswer { installMessage(it.getArgument(0), it.getArgument(1)) }
    on { selectDatasetInstallMessages(any()) } doAnswer { installMessages(it.getArgument(0)) }
    on { selectDatasetSyncControlRecord(any()) } doAnswer { syncControl(it.getArgument(0)) }
    on { selectDatasetVisibilityRecords(any()) } doAnswer { visibilityRecords(it.getArgument(0)) }
    on { selectDatasetProjectLinks(any()) } doAnswer { projectLinks(it.getArgument(0)) }
    on { streamAllSyncControlRecords() } doAnswer { ReconcilerStream(reconcilerRecords()) }
    on { selectDatasetsByInstallStatus(any(), any()) } doAnswer { byInstallStatus(it.getArgument(0), it.getArgument(1)) }
    on { testDatasetVisibilityExists(any(), any()) } doAnswer { visibilityExists(it.getArgument(0), it.getArgument(1)) }
    on { testDatasetProjectLinkExists(any(), any()) } doAnswer { projectLinkExists(it.getArgument(0), it.getArgument(1)) }
    on { deleteDataset(any()) } doAnswer { onDeleteDataset(it.getArgument(0)) }
    on { deleteSyncControl(any()) } doAnswer { onDeleteSyncControl(it.getArgument(0)) }
    on { deleteInstallMessages(any()) } doAnswer { onDeleteInstallMessages(it.getArgument(0)) }
    on { deleteInstallMessage(any(), any()) } doAnswer { onDeleteInstallMessage(it.getArgument(0), it.getArgument(1)) }
    on { deleteDatasetVisibilities(any()) } doAnswer { onDeleteVisibilities(it.getArgument(0)) }
    on { deleteDatasetVisibility(any(), any()) } doAnswer { onDeleteVisibility(it.getArgument(0), it.getArgument(1)) }
    on { deleteDatasetProjectLinks(any()) } doAnswer { onDeleteProjectLinks(it.getArgument(0)) }
    on { deleteDatasetProjectLink(any(), any()) } doAnswer { onDeleteProjectLink(it.getArgument(0), it.getArgument(1)) }
    on { deleteDatasetMeta(any()) } doAnswer { onDeleteMeta(it.getArgument(0)) }
    on { insertDataset(any()) } doAnswer { onInsertDataset(it.getArgument(0)) }
    on { insertDatasetInstallMessage(any()) } doAnswer { onInsertInstallMessage(it.getArgument(0)) }
    on { insertDatasetProjectLink(any(), any()) } doAnswer { onInsertProjectLink(it.getArgument(0), it.getArgument(1)) }
    on { insertDatasetProjectLinks(any(), any()) } doAnswer { onInsertProjectLinks(it.getArgument(0), it.getArgument(1)) }
    on { insertDatasetVisibility(any(), any()) } doAnswer { onInsertVisibility(it.getArgument(0), it.getArgument(1)) }
    on { insertSyncControl(any()) } doAnswer { onInsertSyncControl(it.getArgument(0)) }
    on { insertDatasetMeta(any(), any(), any()) } doAnswer { onInsertMeta(it.getArgument(0), it.getArgument(1), it.getArgument(3)) }
    on { updateDataset(any()) } doAnswer { onUpdateDataset(it.getArgument(0)) }
    on { updateDatasetDeletedFlag(any(), any()) } doAnswer { onUpdateDeleteFlag(it.getArgument(0), it.getArgument(1)) }
    on { updateSyncControlDataTimestamp(any(), any()) } doAnswer { onUpdateSyncControlData(it.getArgument(0), it.getArgument(1)) }
    on { updateSyncControlMetaTimestamp(any(), any()) } doAnswer { onUpdateSyncControlMeta(it.getArgument(0), it.getArgument(1)) }
    on { updateSyncControlSharesTimestamp(any(), any()) } doAnswer { onUpdateSyncControlShares(it.getArgument(0), it.getArgument(1)) }
    on { updateDatasetInstallMessage(any()) } doAnswer { onUpdateInstallMessage(it.getArgument(0)) }
    on { updateDatasetMeta(any(), any(), any()) } doAnswer { onUpdateMeta(it.getArgument(0), it.getArgument(1), it.getArgument(2)) }
    on { upsertDatasetMeta(any(), any(), any()) } doAnswer { onUpsertMeta(it.getArgument(0), it.getArgument(1), it.getArgument(2)) }
    on { rollback() } doAnswer { onRollback() }
    on { commit() } doAnswer { onCommit() }
  }

fun mockVisibilityRecord(
  datasetID: DatasetID? = null,
  userID: UserID? = null,
): DatasetVisibilityRecord =
  mock {
    datasetID?.also { on { this.datasetID } doReturn it }
    userID?.also { on { this.userID } doReturn it }
  }

fun mockProjectLinkRecord(
  datasetID: DatasetID? = null,
  projectID: ProjectID? = null,
): DatasetProjectLinkRecord =
  mock {
    datasetID?.also { on { this.datasetID } doReturn it }
    projectID?.also { on { this.projectID } doReturn it }
  }

fun mockInstallMessage(
  datasetID: DatasetID? = null,
  installType: InstallType? = null,
  status: InstallStatus? = null,
  message: String? = null,
  updated: OffsetDateTime? = null,
): DatasetInstallMessage =
  mock {
    datasetID?.also { on { this.datasetID } doReturn it }
    installType?.also { on { this.installType } doReturn it }
    status?.also { on { this.status } doReturn it }
    on { this.message } doReturn message
    updated?.also { on { this.updated } doReturn it }
  }

fun mockAppDatasetRecord(
  datasetID: DatasetID? = null,
  owner: UserID? = null,
  typeName: String? = null,
  typeVersion: String? = null,
  isDeleted: DeleteFlag? = null,
): DatasetRecord =
  mock {
    datasetID?.also { on { this.datasetID } doReturn it }
    owner?.also { on { this.owner } doReturn it }
    typeName?.also { on { this.typeName } doReturn typeName }
    typeVersion?.also { on { this.typeVersion } doReturn typeVersion }
    isDeleted?.also { on { this.isDeleted } doReturn isDeleted }
  }
