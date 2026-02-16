package vdi.core.db.cache

import java.sql.Connection
import vdi.core.db.cache.model.BrokenImportListQuery
import vdi.core.db.cache.query.AdminAllDatasetsQuery
import vdi.core.db.cache.query.DatasetListQuery
import vdi.core.db.cache.sql.complex.DatasetAdminQueries
import vdi.core.db.cache.sql.complex.DatasetQueries
import vdi.core.db.cache.sql.complex.DatasetShareQueries
import vdi.core.db.cache.sql.complex.ReconcilerQueries
import vdi.core.db.cache.sql.simple.*
import vdi.model.DatasetUploadStatus
import vdi.model.meta.*

internal sealed class CacheDBImplBase: CacheDB {
  protected abstract val connection: Connection

  protected abstract fun <T> runQuery(fn: Connection.() -> T): T

  override fun selectAcceptedSharesForUser(recipientID: UserID) =
    runQuery { DatasetShareQueries.select(recipientID, DatasetShareOffer.Action.Grant, DatasetShareReceipt.Action.Accept) }

  override fun selectAdminAllDatasets(query: AdminAllDatasetsQuery) =
    runQuery { DatasetAdminQueries.select(query) }

  override fun selectAdminAllDatasetCount(query: AdminAllDatasetsQuery) =
    runQuery { DatasetAdminQueries.count(query) }

  override fun selectAdminDatasetDetails(datasetID: DatasetID) =
    runQuery { DatasetAdminQueries.select(datasetID) }

  override fun selectDataset(datasetID: DatasetID) =
    runQuery { DatasetQueries.select(datasetID) }

  override fun selectDatasetsByProjectName(ownerID: UserID, projectName: String) =
    runQuery { DatasetQueries.selectByProjectName(ownerID, projectName) }

  override fun selectDatasetsByProgramName(ownerID: UserID, programName: String) =
    runQuery { DatasetQueries.selectByProgramName(ownerID, programName) }

  override fun selectDatasetsByCommonPublication(rootDatasetID: DatasetID) =
    runQuery { DatasetQueries.selectByCommonPublication(rootDatasetID) }

  override fun selectDatasetForUser(userID: UserID, datasetID: DatasetID) =
    runQuery { DatasetQueries.select(userID, datasetID) }

  override fun selectDatasetList(query: DatasetListQuery) =
    runQuery { DatasetQueries.select(query) }

  override fun selectInstallFiles(datasetID: DatasetID) =
    runQuery { DatasetInstallFiles.select(datasetID) }

  override fun selectInstallFileCount(datasetID: DatasetID) =
    runQuery { DatasetInstallFiles.count(datasetID) }

  override fun selectNonPrivateDatasets() =
    runQuery { DatasetQueries.selectNonPrivateDatasets() }

  override fun selectPublications(datasetID: DatasetID): List<DatasetPublication> =
    runQuery { DatasetPublicationsTable.select(datasetID) }

  override fun selectUploadFiles(datasetID: DatasetID) =
    runQuery { DatasetUploadFiles.select(datasetID) }

  override fun selectUndeletedDatasetIDsForUser(userID: UserID) =
    runQuery { DatasetsTable.selectIDs(userID) }

  override fun selectUploadFileCount(datasetID: DatasetID) =
    runQuery { DatasetUploadFiles.count(datasetID) }

  override fun selectUploadFileSummaries(datasetIDs: List<DatasetID>) =
    runQuery { DatasetUploadFiles.selectSummaries(datasetIDs) }

  override fun selectSharesForDataset(datasetID: DatasetID) =
    runQuery { DatasetShareQueries.select(datasetID) }

  override fun selectSharesForDatasets(datasetIDs: List<DatasetID>) =
    runQuery { DatasetShareQueries.select(datasetIDs) }

  override fun selectImportControl(datasetID: DatasetID) =
    runQuery { ImportControlTable.select(datasetID) }

  override fun selectImportMessages(datasetID: DatasetID) =
    runQuery { ImportMessagesTable.select(datasetID) }

  override fun selectSyncControl(datasetID: DatasetID) =
    runQuery { SyncControlTable.select(datasetID) }

  override fun selectDeletedDatasets() =
    runQuery { DatasetQueries.selectDeleted() }

  override fun selectOpenSharesForUser(recipientID: UserID) =
    runQuery { DatasetShareQueries.selectOpenShares(recipientID) }

  override fun selectRejectedSharesForUser(recipientID: UserID) =
    runQuery { DatasetShareQueries.select(recipientID, DatasetShareOffer.Action.Grant, DatasetShareReceipt.Action.Reject) }

  override fun selectAllSharesForUser(recipientID: UserID) =
    runQuery { DatasetShareQueries.select(recipientID) }

  override fun selectUploadStatus(datasetID: DatasetID): DatasetUploadStatus? =
    runQuery { UploadStatus.select(datasetID) }

  // NOTE: THE CALLER IS RESPONSIBLE FOR CLOSING THE CONNECTION!
  override fun selectAllSyncControlRecords() =
    context(connection) { ReconcilerQueries.select() }

  override fun selectBrokenDatasetImports(query: BrokenImportListQuery) =
    runQuery { DatasetAdminQueries.selectBrokenImports(query) }

  override fun selectLatestRevision(datasetID: DatasetID, includeDeleted: Boolean) =
    runQuery { DatasetRevisionsTable.selectLatestRevision(datasetID, includeDeleted) }

  override fun selectOriginalDatasetID(datasetID: DatasetID) =
    runQuery { DatasetRevisionsTable.selectOriginalDatasetID(datasetID) }

  override fun selectRevisions(datasetID: DatasetID) =
    runQuery { DatasetRevisionsTable.selectRevisionHistory(datasetID) }

  override fun openTransaction(): CacheDBTransaction =
    CacheDBTransactionImpl(dataSource, connection.apply { autoCommit = false }, details)

  protected fun makeJDBCPostgresConnectionString(host: String, port: UShort, name: String) =
    "jdbc:postgresql://$host:$port/$name"
}