package org.veupathdb.vdi.lib.db.cache

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import org.veupathdb.vdi.lib.db.cache.model.*
import org.veupathdb.vdi.lib.db.cache.query.AdminAllDatasetsQuery

fun CacheDB(): CacheDB = CacheDBImpl

interface CacheDB {

  fun tempMigrateDB()

  fun selectDataset(datasetID: DatasetID): DatasetRecord?

  fun selectInstallFiles(datasetID: DatasetID): List<DatasetFile>

  fun selectUploadFiles(datasetID: DatasetID): List<DatasetFile>

  fun selectAdminAllDatasetCount(query: AdminAllDatasetsQuery): UInt

  fun selectAdminAllDatasets(query: AdminAllDatasetsQuery): List<AdminAllDatasetsRow>

  fun selectAdminDatasetDetails(datasetID: DatasetID): AdminDatasetDetailsRecord?

  fun selectInstallFileCount(datasetID: DatasetID): Int

  fun selectInstallFileSummaries(datasetIDs: List<DatasetID>): Map<DatasetID, DatasetFileSummary>

  fun selectUploadFileCount(datasetID: DatasetID): Int

  fun selectUploadFileSummaries(datasetIDs: List<DatasetID>): Map<DatasetID, DatasetFileSummary>

  fun selectDatasetList(query: DatasetListQuery): List<DatasetRecord>

  fun selectDatasetForUser(userID: UserID, datasetID: DatasetID): DatasetRecord?

  fun selectDatasetsForUser(userID: UserID): List<DatasetRecord>

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

inline fun CacheDB.withTransaction(fn: (CacheDBTransaction) -> Unit) =
  openTransaction().use {
    try {
      fn(it)
    } catch (e: Throwable) {
      it.rollback()
    }
  }

