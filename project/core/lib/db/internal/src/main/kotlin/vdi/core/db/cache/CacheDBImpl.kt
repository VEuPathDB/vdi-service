package vdi.core.db.cache

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import javax.sql.DataSource
import vdi.core.config.loadAndCacheStackConfig
import vdi.core.db.cache.health.DatabaseDependency
import vdi.core.db.cache.model.BrokenImportListQuery
import vdi.core.db.cache.query.DatasetListQuery
import vdi.core.db.cache.query.AdminAllDatasetsQuery
import vdi.core.db.cache.sql.dataset_revisions.selectDatasetRevisions
import vdi.core.db.cache.sql.dataset_revisions.selectLatestDatasetRevision
import vdi.core.db.cache.sql.dataset_revisions.selectOriginalDatasetID
import vdi.core.db.cache.sql.dataset_share_offers.selectAllSharesFor
import vdi.core.db.cache.sql.datasets.selectNonPrivateDatasets
import vdi.core.db.cache.sql.dataset_share_offers.selectOpenSharesFor
import vdi.core.db.cache.sql.dataset_share_offers.selectSharesFor
import vdi.core.db.cache.sql.datasets.*
import vdi.core.db.cache.sql.datasets.selectDataset
import vdi.core.db.cache.sql.datasets.selectDatasetForUser
import vdi.core.db.cache.sql.datasets.selectDeletedDatasets
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
import vdi.core.err.StartupException
import vdi.core.health.Dependency
import vdi.core.health.RemoteDependencies
import vdi.logging.MetaLogger
import vdi.model.data.DatasetShareOffer
import vdi.model.data.DatasetShareReceipt
import org.postgresql.Driver as PostgresDriver

internal object CacheDBImpl: CacheDB {

  override val details: CacheDBConnectionDetails

  override val dataSource: DataSource

  private val connection
    get() = dataSource.connection

  init {
    dataSource = loadAndCacheStackConfig().vdi.cacheDB.let {
      MetaLogger.info("initializing datasource for cache-db")

      details = CacheDBConnectionDetails(it)

      HikariConfig()
        .apply {
          jdbcUrl  = makeJDBCPostgresConnectionString(details.host, details.port, details.name)
          username = it.username
          password = it.password.asString
          maximumPoolSize = it.poolSize?.toInt() ?: 5
          driverClassName = PostgresDriver::class.java.name
        }
        .let(::HikariDataSource)
    }

    RemoteDependencies.register(DatabaseDependency(this).also {
      when (it.checkStatus()) {
        Dependency.Status.NotOk,
        Dependency.Status.Unknown -> throw StartupException("could not connect to cache db")
        else                      -> {}
      }
    })
  }

  override fun selectDataset(datasetID: DatasetID) =
    connection.use { it.selectDataset(datasetID) }

  override fun selectInstallFiles(datasetID: DatasetID) =
    connection.use { it.selectInstallFiles(datasetID) }

  override fun selectUploadFiles(datasetID: DatasetID) =
    connection.use { it.selectUploadFiles(datasetID) }

  override fun selectAdminAllDatasetCount(query: AdminAllDatasetsQuery) =
    connection.use { it.selectAdminAllDatasetCount(query) }

  override fun selectAdminAllDatasets(query: AdminAllDatasetsQuery) =
    connection.use { it.selectAdminAllDatasets(query) }

  override fun selectAdminDatasetDetails(datasetID: DatasetID) =
    connection.use { it.selectAdminDatasetDetails(datasetID) }

  override fun selectInstallFileCount(datasetID: DatasetID) =
    connection.use { it.selectInstallFileCount(datasetID) }

  override fun selectUploadFileCount(datasetID: DatasetID) =
    connection.use { it.selectUploadFileCount(datasetID) }

  override fun selectUploadFileSummaries(datasetIDs: List<DatasetID>) =
    connection.use { it.selectUploadFileSummaries(datasetIDs) }

  override fun selectDatasetList(query: DatasetListQuery) =
    connection.use { it.selectDatasetList(query) }

  override fun selectDatasetForUser(userID: UserID, datasetID: DatasetID) =
    connection.use { it.selectDatasetForUser(userID, datasetID) }

  override fun selectUndeletedDatasetIDsForUser(userID: UserID) =
    connection.use { it.selectUndeletedDatasetIDsForUser(userID) }

  override fun selectNonPrivateDatasets() =
    connection.use { it.selectNonPrivateDatasets() }

  override fun selectSharesForDataset(datasetID: DatasetID) =
    connection.use { it.selectSharesFor(datasetID) }

  override fun selectSharesForDatasets(datasetIDs: List<DatasetID>) =
    connection.use { it.selectSharesFor(datasetIDs) }

  override fun selectImportControl(datasetID: DatasetID) =
    connection.use { it.selectImportControl(datasetID) }

  override fun selectImportMessages(datasetID: DatasetID) =
    connection.use { it.selectImportMessages(datasetID) }

  override fun selectSyncControl(datasetID: DatasetID) =
    connection.use { it.selectSyncControl(datasetID) }

  override fun selectDeletedDatasets() =
    connection.use { it.selectDeletedDatasets() }

  override fun selectOpenSharesForUser(recipientID: UserID) =
    connection.use { it.selectOpenSharesFor(recipientID) }

  override fun selectAcceptedSharesForUser(recipientID: UserID) =
    connection.use { it.selectSharesFor(recipientID, DatasetShareOffer.Action.Grant, DatasetShareReceipt.Action.Accept) }

  override fun selectRejectedSharesForUser(recipientID: UserID) =
    connection.use { it.selectSharesFor(recipientID, DatasetShareOffer.Action.Grant, DatasetShareReceipt.Action.Reject) }

  override fun selectAllSharesForUser(recipientID: UserID) =
    connection.use { it.selectAllSharesFor(recipientID) }

  // NOTE: THE CALLER IS RESPONSIBLE FOR CLOSING THE CONNECTION!
  override fun selectAllSyncControlRecords() =
    connection.selectAllSyncControl()

  override fun selectBrokenDatasetImports(query: BrokenImportListQuery) =
    connection.use { it.selectBrokenImports(query) }

  override fun selectLatestRevision(datasetID: DatasetID, includeDeleted: Boolean) =
    connection.use { it.selectLatestDatasetRevision(datasetID, includeDeleted) }

  override fun selectOriginalDatasetID(datasetID: DatasetID) =
    connection.use { it.selectOriginalDatasetID(datasetID) }

  override fun selectRevisions(datasetID: DatasetID) =
    connection.use { it.selectDatasetRevisions(datasetID) }

  override fun openTransaction() =
    CacheDBTransactionImpl(connection.apply { autoCommit = false })

  private fun makeJDBCPostgresConnectionString(host: String, port: UShort, name: String) =
    "jdbc:postgresql://$host:$port/$name"

}

