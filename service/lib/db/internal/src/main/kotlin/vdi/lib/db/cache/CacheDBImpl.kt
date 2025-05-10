package vdi.lib.db.cache

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import javax.sql.DataSource
import vdi.lib.config.loadAndCacheStackConfig
import vdi.lib.db.cache.health.DatabaseDependency
import vdi.lib.db.cache.model.BrokenImportListQuery
import vdi.lib.db.cache.model.DatasetListQuery
import vdi.lib.db.cache.query.AdminAllDatasetsQuery
import vdi.lib.db.cache.sql.select.*
import vdi.lib.err.StartupException
import vdi.lib.health.Dependency
import vdi.lib.health.RemoteDependencies
import org.postgresql.Driver as PostgresDriver

internal object CacheDBImpl: CacheDB {

  override val details: CacheDBConnectionDetails

  override val dataSource: DataSource

  private val log = LoggerFactory.getLogger(javaClass)

  private val connection
    get() = dataSource.connection

  init {
    dataSource = loadAndCacheStackConfig().vdi.cacheDB.let {
      log.info("initializing datasource for cache-db")

      details = CacheDBConnectionDetails(it)

      HikariConfig()
        .apply {
          jdbcUrl  = makeJDBCPostgresConnectionString(details.host, details.port, details.name)
          username = it.username
          password = it.password.unwrap()
          maximumPoolSize = it.poolSize?.toInt() ?: 5
          driverClassName = PostgresDriver::class.java.name
        }
        .let(::HikariDataSource)
    }

    RemoteDependencies.register(DatabaseDependency(this).also {
      when (it.checkStatus()) {
        Dependency.Status.NotOk,
        Dependency.Status.Unknown -> throw StartupException("could not connect to cache db")
        else -> {}
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

  override fun selectInstallFileSummaries(datasetIDs: List<DatasetID>) =
    connection.use { it.selectInstallFileSummaries(datasetIDs) }

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
    connection.use { it.selectAcceptedSharesFor(recipientID) }

  override fun selectRejectedSharesForUser(recipientID: UserID) =
    connection.use { it.selectRejectedSharesFor(recipientID) }

  override fun selectAllSharesForUser(recipientID: UserID) =
    connection.use { it.selectAllSharesFor(recipientID) }

  // NOTE: THE CALLER IS RESPONSIBLE FOR CLOSING THE CONNECTION!
  override fun selectAllSyncControlRecords() =
    connection.selectAllSyncControl()

  override fun selectBrokenDatasetImports(query: BrokenImportListQuery) =
    connection.use { it.selectBrokenImports(query) }

  override fun selectLatestRevision(datasetID: DatasetID) =
    connection.use { it.selectLatestDatasetRevision(datasetID) }

  override fun selectOriginalDatasetID(datasetID: DatasetID) =
    connection.use { it.selectOriginalDatasetID(datasetID) }

  override fun selectRevisions(datasetID: DatasetID) =
    connection.use { it.selectDatasetRevisions(datasetID) }

  override fun openTransaction() =
    CacheDBTransactionImpl(connection.apply { autoCommit = false })

  private fun makeJDBCPostgresConnectionString(host: String, port: UShort, name: String) =
    "jdbc:postgresql://$host:$port/$name"

}

