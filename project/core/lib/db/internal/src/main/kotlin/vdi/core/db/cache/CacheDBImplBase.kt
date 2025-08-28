package vdi.core.db.cache

import java.sql.Connection
import vdi.core.db.cache.model.BrokenImportListQuery
import vdi.core.db.cache.model.RelatedDataset
import vdi.core.db.cache.query.AdminAllDatasetsQuery
import vdi.core.db.cache.query.DatasetListQuery
import vdi.core.db.cache.sql.dataset_publications.selectPublicationsForDataset
import vdi.core.db.cache.sql.dataset_revisions.selectDatasetRevisions
import vdi.core.db.cache.sql.dataset_revisions.selectLatestDatasetRevision
import vdi.core.db.cache.sql.dataset_revisions.selectOriginalDatasetID
import vdi.core.db.cache.sql.dataset_share_offers.selectAllSharesFor
import vdi.core.db.cache.sql.dataset_share_offers.selectOpenSharesFor
import vdi.core.db.cache.sql.dataset_share_offers.selectSharesFor
import vdi.core.db.cache.sql.datasets.*
import vdi.core.db.cache.sql.import_control.selectBrokenImports
import vdi.core.db.cache.sql.import_control.selectImportControl
import vdi.core.db.cache.sql.import_messages.selectImportMessages
import vdi.core.db.cache.sql.install_files.selectInstallFileCount
import vdi.core.db.cache.sql.install_files.selectInstallFiles
import vdi.core.db.cache.sql.sync_control.selectAllSyncControl
import vdi.core.db.cache.sql.sync_control.selectSyncControl
import vdi.core.db.cache.sql.upload_files.selectUploadFileCount
import vdi.core.db.cache.sql.upload_files.selectUploadFileSummaries
import vdi.core.db.cache.sql.upload_files.selectUploadFiles
import vdi.model.data.*

sealed class CacheDBImplBase: CacheDB {
  protected abstract val connection: Connection

  protected abstract fun <T> runQuery(fn: Connection.() -> T): T

  override fun selectAcceptedSharesForUser(recipientID: UserID) =
    runQuery { selectSharesFor(recipientID, DatasetShareOffer.Action.Grant, DatasetShareReceipt.Action.Accept) }

  override fun selectAdminAllDatasets(query: AdminAllDatasetsQuery) =
    runQuery { selectAdminAllDatasets(query) }

  override fun selectAdminAllDatasetCount(query: AdminAllDatasetsQuery) =
    runQuery { selectAdminAllDatasetCount(query) }

  override fun selectAdminDatasetDetails(datasetID: DatasetID) =
    runQuery { selectAdminDatasetDetails(datasetID) }

  override fun selectDataset(datasetID: DatasetID) =
    runQuery { selectDataset(datasetID) }

  override fun selectDatasetsByProjectName(ownerID: UserID, projectName: String) =
    runQuery { selectDatasetsByProjectName(ownerID, projectName) }

  override fun selectDatasetsByProgramName(ownerID: UserID, programName: String) =
    runQuery { selectDatasetsByProgramName(ownerID, programName) }

  override fun selectDatasetsByCommonPublication(rootDatasetID: DatasetID) =
    runQuery { selectDatasetsByCommonPublication(rootDatasetID) }

  override fun selectDatasetForUser(userID: UserID, datasetID: DatasetID) =
    runQuery { selectDatasetForUser(userID, datasetID) }

  override fun selectDatasetList(query: DatasetListQuery) =
    runQuery { selectDatasetList(query) }

  override fun selectInstallFiles(datasetID: DatasetID) =
    runQuery { selectInstallFiles(datasetID) }

  override fun selectInstallFileCount(datasetID: DatasetID) =
    runQuery { selectInstallFileCount(datasetID) }

  override fun selectNonPrivateDatasets() =
    runQuery { selectNonPrivateDatasets() }

  override fun selectPublications(datasetID: DatasetID): List<DatasetPublication> =
    runQuery { selectPublicationsForDataset(datasetID) }

  override fun selectRelatedDatasets(ownerID: UserID, datasetID: DatasetID): List<RelatedDataset> =
    runQuery { selectRelatedDatasets() }

  override fun selectUploadFiles(datasetID: DatasetID) =
    runQuery { selectUploadFiles(datasetID) }

  override fun selectUndeletedDatasetIDsForUser(userID: UserID) =
    runQuery { selectUndeletedDatasetIDsForUser(userID) }

  override fun selectUploadFileCount(datasetID: DatasetID) =
    runQuery { selectUploadFileCount(datasetID) }

  override fun selectUploadFileSummaries(datasetIDs: List<DatasetID>) =
    runQuery { selectUploadFileSummaries(datasetIDs) }

  override fun selectSharesForDataset(datasetID: DatasetID) =
    runQuery { selectSharesFor(datasetID) }

  override fun selectSharesForDatasets(datasetIDs: List<DatasetID>) =
    runQuery { selectSharesFor(datasetIDs) }

  override fun selectImportControl(datasetID: DatasetID) =
    runQuery { selectImportControl(datasetID) }

  override fun selectImportMessages(datasetID: DatasetID) =
    runQuery { selectImportMessages(datasetID) }

  override fun selectSyncControl(datasetID: DatasetID) =
    runQuery { selectSyncControl(datasetID) }

  override fun selectDeletedDatasets() =
    runQuery { selectDeletedDatasets() }

  override fun selectOpenSharesForUser(recipientID: UserID) =
    runQuery { selectOpenSharesFor(recipientID) }

  override fun selectRejectedSharesForUser(recipientID: UserID) =
    runQuery { selectSharesFor(recipientID, DatasetShareOffer.Action.Grant, DatasetShareReceipt.Action.Reject) }

  override fun selectAllSharesForUser(recipientID: UserID) =
    runQuery { selectAllSharesFor(recipientID) }

  // NOTE: THE CALLER IS RESPONSIBLE FOR CLOSING THE CONNECTION!
  override fun selectAllSyncControlRecords() =
    connection.selectAllSyncControl()

  override fun selectBrokenDatasetImports(query: BrokenImportListQuery) =
    runQuery { selectBrokenImports(query) }

  override fun selectLatestRevision(datasetID: DatasetID, includeDeleted: Boolean) =
    runQuery { selectLatestDatasetRevision(datasetID, includeDeleted) }

  override fun selectOriginalDatasetID(datasetID: DatasetID) =
    runQuery { selectOriginalDatasetID(datasetID) }

  override fun selectRevisions(datasetID: DatasetID) =
    runQuery { selectDatasetRevisions(datasetID) }

  override fun openTransaction(): CacheDBTransaction =
    CacheDBTransactionImpl(connection.apply { autoCommit = false })

  protected fun makeJDBCPostgresConnectionString(host: String, port: UShort, name: String) =
    "jdbc:postgresql://$host:$port/$name"
}