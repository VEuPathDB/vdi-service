package vdi.lib.db.cache

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetFileInfo
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import javax.sql.DataSource
import vdi.lib.db.cache.model.*
import vdi.lib.db.cache.query.AdminAllDatasetsQuery
import vdi.lib.db.model.ReconcilerTargetRecord
import vdi.lib.db.model.SyncControlRecord

fun CacheDB(): CacheDB = CacheDBImpl

interface CacheDB {

  val details: CacheDBConnectionDetails

  val dataSource: DataSource

  /**
   * Attempts to fetch a dataset by [ID][datasetID], returning `null` if no such
   * dataset was found.
   */
  fun selectDataset(datasetID: DatasetID): DatasetRecord?

  /**
   * Fetches the list of install-ready files produced by the import process for
   * the target dataset.
   *
   * If the dataset failed import or has not yet been imported, this method will
   * return an empty list.
   */
  fun selectInstallFiles(datasetID: DatasetID): List<VDIDatasetFileInfo>

  /**
   * Fetches the list of import-ready files uploaded by the user.
   */
  fun selectUploadFiles(datasetID: DatasetID): List<VDIDatasetFileInfo>

  fun selectAdminAllDatasetCount(query: AdminAllDatasetsQuery): UInt

  fun selectAdminAllDatasets(query: AdminAllDatasetsQuery): List<AdminAllDatasetsRow>

  fun selectAdminDatasetDetails(datasetID: DatasetID): AdminDatasetDetailsRecord?

  fun selectInstallFileCount(datasetID: DatasetID): Int

  fun selectInstallFileSummaries(datasetIDs: List<DatasetID>): Map<DatasetID, DatasetFileSummary>

  fun selectUploadFileCount(datasetID: DatasetID): Int

  fun selectUploadFileSummaries(datasetIDs: List<DatasetID>): Map<DatasetID, DatasetFileSummary>

  fun selectDatasetList(query: DatasetListQuery): List<DatasetRecord>

  /**
   * Attempts to fetch a dataset by [ID][datasetID] that is visible to the user
   * identified by the given [user ID][userID].
   *
   * Datasets may be visible if they are either owned by, or shared with the
   * target user.
   *
   * If no dataset could be found that is visible to the target user, `null`
   * will be returned.
   */
  fun selectDatasetForUser(userID: UserID, datasetID: DatasetID): DatasetRecord?

  fun selectUndeletedDatasetIDsForUser(userID: UserID): List<DatasetID>

  fun selectNonPrivateDatasets(): List<DatasetRecord>

  fun selectSharesForDataset(datasetID: DatasetID): List<DatasetShare>

  fun selectSharesForDatasets(datasetIDs: List<DatasetID>): Map<DatasetID, List<DatasetShare>>

  fun selectImportControl(datasetID: DatasetID): DatasetImportStatus?

  fun selectImportMessages(datasetID: DatasetID): List<String>

  fun selectSyncControl(datasetID: DatasetID): SyncControlRecord?

  fun selectDeletedDatasets(): List<DeletedDataset>

  fun selectOpenSharesForUser(recipientID: UserID): List<DatasetShareListEntry>

  fun selectAcceptedSharesForUser(recipientID: UserID): List<DatasetShareListEntry>

  fun selectRejectedSharesForUser(recipientID: UserID): List<DatasetShareListEntry>

  fun selectAllSharesForUser(recipientID: UserID): List<DatasetShareListEntry>

  /**
   * Streams sorted stream of all dataset control records.
   *
   * **NOTE**: THE CALLER IS RESPONSIBLE FOR CLOSING THE RETURNED VALUE TO AVOID
   * CONNECTION LEAKS!
   *
   * @return Stream of dataset control records sorted by user ID and then dataset ID. The stream
   * must be closed to release the db connection.
   */
  fun selectAllSyncControlRecords(): CloseableIterator<ReconcilerTargetRecord>

  fun selectLatestRevision(datasetID: DatasetID): DatasetRevisionRecord?

  fun selectOriginalDatasetID(datasetID: DatasetID): DatasetID

  fun selectRevisions(datasetID: DatasetID): DatasetRevisionRecordSet?

  fun selectBrokenDatasetImports(query: BrokenImportListQuery): List<BrokenImportRecord>

  fun openTransaction(): CacheDBTransaction
}
