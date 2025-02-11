package vdi.component.db.cache

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.env.optUByte
import org.veupathdb.vdi.lib.common.env.optUShort
import org.veupathdb.vdi.lib.common.env.require
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.cache.model.BrokenImportListQuery
import vdi.component.db.cache.model.DatasetListQuery
import vdi.component.db.cache.query.AdminAllDatasetsQuery
import vdi.component.db.cache.sql.select.*
import vdi.component.env.EnvKey
import javax.sql.DataSource
import org.postgresql.Driver as PostgresDriver

internal object CacheDBImpl: CacheDB {

  override val details: CacheDBConnectionDetails

  override val dataSource: DataSource

  private val log = LoggerFactory.getLogger(javaClass)

  private val connection
    get() = dataSource.connection

  init {
    val env = System.getenv()

    val host     = env.require(EnvKey.CacheDB.Host)
    val port     = env.optUShort(EnvKey.CacheDB.Port) ?: CacheDBConfigDefaults.Port
    val name     = env.require(EnvKey.CacheDB.Name)
    val username = env.require(EnvKey.CacheDB.Username)
    val password = env.require(EnvKey.CacheDB.Password)
    val poolSize = env.optUByte(EnvKey.CacheDB.PoolSize) ?: CacheDBConfigDefaults.PoolSize

    log.info("initializing datasource for cache-db")

    val config = HikariConfig()
      .also {
        it.jdbcUrl = makeJDBCPostgresConnectionString(host, port, name)
        it.username = username
        it.password = password
        it.maximumPoolSize = poolSize.toInt()
        it.driverClassName = PostgresDriver::class.java.name
      }

    dataSource = HikariDataSource(config)
    details    = CacheDBConnectionDetails(host, port, name)
  }

  override fun selectDataset(datasetID: DatasetID) = connection.use { it.selectDataset(datasetID) }

  override fun selectInstallFiles(datasetID: DatasetID) = connection.use { it.selectInstallFiles(datasetID) }

  override fun selectUploadFiles(datasetID: DatasetID) = connection.use { it.selectUploadFiles(datasetID) }

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

  override fun selectDatasetList(query: DatasetListQuery) = connection.use { it.selectDatasetList(query) }

  override fun selectDatasetForUser(userID: UserID, datasetID: DatasetID) =
    connection.use { it.selectDatasetForUser(userID, datasetID) }

  override fun selectUndeletedDatasetIDsForUser(userID: UserID) = connection.use { it.selectUndeletedDatasetIDsForUser(userID) }

  override fun selectNonPrivateDatasets() = connection.use { it.selectNonPrivateDatasets() }

  override fun selectSharesForDataset(datasetID: DatasetID) = connection.use { it.selectSharesFor(datasetID) }

  override fun selectSharesForDatasets(datasetIDs: List<DatasetID>) = connection.use { it.selectSharesFor(datasetIDs) }

  override fun selectImportControl(datasetID: DatasetID) = connection.use { it.selectImportControl(datasetID) }

  override fun selectImportMessages(datasetID: DatasetID) = connection.use { it.selectImportMessages(datasetID) }

  override fun selectSyncControl(datasetID: DatasetID) = connection.use { it.selectSyncControl(datasetID) }

  override fun selectDeletedDatasets() = connection.use { it.selectDeletedDatasets() }

  override fun selectOpenSharesForUser(recipientID: UserID) = connection.use { it.selectOpenSharesFor(recipientID) }

  override fun selectAcceptedSharesForUser(recipientID: UserID) =
    connection.use { it.selectAcceptedSharesFor(recipientID) }

  override fun selectRejectedSharesForUser(recipientID: UserID) =
    connection.use { it.selectRejectedSharesFor(recipientID) }

  override fun selectAllSharesForUser(recipientID: UserID) = connection.use { it.selectAllSharesFor(recipientID) }

  override fun selectAllSyncControlRecords() = connection.selectAllSyncControl()

  override fun selectBrokenDatasetImports(query: BrokenImportListQuery) = connection.use { it.selectBrokenImports(query) }

  override fun openTransaction() =
    CacheDBTransactionImpl(connection.apply { autoCommit = false })

  private fun makeJDBCPostgresConnectionString(host: String, port: UShort, name: String) =
    "jdbc:postgresql://$host:$port/$name"
}

