package vdi.test

import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import vdi.component.db.app.AppDB
import vdi.component.db.app.AppDBAccessor
import vdi.component.db.app.AppDBTransaction
import vdi.component.db.app.model.*
import java.time.OffsetDateTime

fun mockAppDB(
  accessor: (ProjectID, DataType) -> AppDBAccessor? = ::twoParamNull,
  transaction: (ProjectID, DataType) -> AppDBTransaction? = ::twoParamNull,
  bulkStatuses: (Map<ProjectID, Collection<DatasetID>>) -> Map<DatasetID, Map<ProjectID, InstallStatuses>> = ::oneParamMap,
  datasetStatuses: (DatasetID, Collection<ProjectID>) -> Map<ProjectID, InstallStatuses> = ::twoParamMap,
): AppDB =
  mock {
    on { getDatasetStatuses(any()) } doAnswer { bulkStatuses(it.getArgument(0)) }
    on { getDatasetStatuses(any(), any()) } doAnswer { datasetStatuses(it.getArgument(0), it.getArgument(1)) }
    on { this.accessor(any(), any()) } doAnswer { accessor(it.getArgument(0), it.getArgument(1)) }
    on { this.transaction(any(), any()) } doAnswer { transaction(it.getArgument(0), it.getArgument(1)) }
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
  typeName: DataType? = null,
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
