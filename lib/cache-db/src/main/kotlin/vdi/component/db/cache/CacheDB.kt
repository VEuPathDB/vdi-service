package vdi.component.db.cache

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetFileInfo
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import vdi.component.db.cache.model.*
import vdi.component.db.cache.query.AdminAllDatasetsQuery
import javax.sql.DataSource

fun CacheDB(): CacheDB = CacheDBImpl

interface CacheDB {

  val details: CacheDBConnectionDetails

  val dataSource: DataSource

  fun selectDataset(datasetID: DatasetID): DatasetRecord?

  fun selectInstallFiles(datasetID: DatasetID): List<VDIDatasetFileInfo>

  fun selectUploadFiles(datasetID: DatasetID): List<VDIDatasetFileInfo>

  fun selectAdminAllDatasetCount(query: AdminAllDatasetsQuery): UInt

  fun selectAdminAllDatasets(query: AdminAllDatasetsQuery): List<AdminAllDatasetsRow>

  fun selectAdminDatasetDetails(datasetID: DatasetID): AdminDatasetDetailsRecord?

  fun selectInstallFileCount(datasetID: DatasetID): Int

  fun selectInstallFileSummaries(datasetIDs: List<DatasetID>): Map<DatasetID, DatasetFileSummary>

  fun selectUploadFileCount(datasetID: DatasetID): Int

  fun selectUploadFileSummaries(datasetIDs: List<DatasetID>): Map<DatasetID, DatasetFileSummary>

  fun selectDatasetList(query: DatasetListQuery): List<DatasetRecord>

  fun selectDatasetForUser(userID: UserID, datasetID: DatasetID): DatasetRecord?

  fun selectUndeletedDatasetIDsForUser(userID: UserID): List<DatasetID>

  fun selectNonPrivateDatasets(): List<DatasetRecord>

  fun selectSharesForDataset(datasetID: DatasetID): List<DatasetShare>

  fun selectSharesForDatasets(datasetIDs: List<DatasetID>): Map<DatasetID, List<DatasetShare>>

  fun selectImportControl(datasetID: DatasetID): DatasetImportStatus?

  fun selectImportMessages(datasetID: DatasetID): List<String>

  fun selectSyncControl(datasetID: DatasetID): VDISyncControlRecord?

  fun selectDeletedDatasets(): List<DeletedDataset>

  fun selectOpenSharesForUser(recipientID: UserID): List<DatasetShareListEntry>

  fun selectAcceptedSharesForUser(recipientID: UserID): List<DatasetShareListEntry>

  fun selectRejectedSharesForUser(recipientID: UserID): List<DatasetShareListEntry>

  fun selectAllSharesForUser(recipientID: UserID): List<DatasetShareListEntry>

  /**
   * Streams sorted stream of all dataset control records.
   *
   * @return Stream of dataset control records sorted by user ID and then dataset ID. The stream
   * must be closed to release the db connection.
   */
  fun selectAllSyncControlRecords(): CloseableIterator<VDIReconcilerTargetRecord>

  fun selectBrokenDatasetImports(query: BrokenImportListQuery): List<BrokenImportRecord>

  fun openTransaction(): CacheDBTransaction
}

inline fun <T> CacheDB.withTransaction(fn: (CacheDBTransaction) -> T) =
  openTransaction().use { t ->
    try {
      fn(t).also { t.commit() }
    } catch (e: Throwable) {
      t.rollback()
      throw e
    }
  }
