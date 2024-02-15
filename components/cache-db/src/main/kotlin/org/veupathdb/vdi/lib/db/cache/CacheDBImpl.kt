package org.veupathdb.vdi.lib.db.cache

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import org.veupathdb.vdi.lib.db.cache.model.*
import org.veupathdb.vdi.lib.db.cache.query.AdminAllDatasetsQuery
import org.veupathdb.vdi.lib.db.cache.sql.select.*
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

  override fun selectDataset(datasetID: DatasetID): DatasetRecord? {
    log.debug("selecting dataset {}", datasetID)
    return connection.use { it.selectDataset(datasetID) }
  }

  override fun selectInstallFiles(datasetID: DatasetID): List<DatasetFile> {
    log.debug("selecting install files for dataset {}", datasetID)
    return connection.use { it.selectInstallFiles(datasetID) }
  }

  override fun selectUploadFiles(datasetID: DatasetID): List<DatasetFile> {
    log.debug("selecting upload files for dataset {}", datasetID)
    return connection.use { it.selectUploadFiles(datasetID) }
  }

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

  override fun selectDatasetList(query: DatasetListQuery): List<DatasetRecord> {
    log.debug("selecting dataset list for user {}", query.userID)
    return connection.use { it.selectDatasetList(query) }
  }

  override fun selectDatasetForUser(userID: UserID, datasetID: DatasetID): DatasetRecord? {
    log.debug("selecting dataset {} for user {}", datasetID, userID)
    return connection.use { it.selectDatasetForUser(userID, datasetID) }
  }

  override fun selectDatasetsForUser(userID: UserID): List<DatasetRecord> {
    log.debug("selecting dataset list for user {}", userID)
    return connection.use { it.selectDatasetsForUser(userID) }
  }

  override fun selectNonPrivateDatasets(): List<DatasetRecord> {
    log.debug("selecting list of non-private datasets")
    return connection.use { it.selectNonPrivateDatasets() }
  }

  override fun selectSharesForDataset(datasetID: DatasetID): List<DatasetShare> {
    log.debug("selecting shares for dataset {}", datasetID)
    return connection.use { it.selectSharesFor(datasetID) }
  }

  override fun selectSharesForDatasets(datasetIDs: List<DatasetID>): Map<DatasetID, List<DatasetShare>> {
    log.debug("selecting shares for {} datasets", datasetIDs.size)
    return connection.use { it.selectSharesFor(datasetIDs) }
  }

  override fun selectImportControl(datasetID: DatasetID): DatasetImportStatus? {
    log.debug("selecting import control record for dataset {}", datasetID)
    return connection.use { it.selectImportControl(datasetID) }
  }

  override fun selectImportMessages(datasetID: DatasetID): List<String> {
    log.debug("selecting import messages for dataset {}", datasetID)
    return connection.use { it.selectImportMessages(datasetID) }
  }

  override fun selectSyncControl(datasetID: DatasetID): VDISyncControlRecord? {
    log.debug("selecting sync control record for dataset {}", datasetID)
    return connection.use { it.selectSyncControl(datasetID) }
  }

  override fun selectDeletedDatasets(): List<DeletedDataset> {
    log.debug("selecting deleted datasets")
    return connection.use { it.selectDeletedDatasets() }
  }

  override fun selectOpenSharesForUser(recipientID: UserID): List<DatasetShareListEntry> {
    log.debug("selecting open shares for recipient {}", recipientID)
    return connection.use { it.selectOpenSharesFor(recipientID) }
  }

  override fun selectAcceptedSharesForUser(recipientID: UserID): List<DatasetShareListEntry> {
    log.debug("selecting accepted shares for recipient {}", recipientID)
    return connection.use { it.selectAcceptedSharesFor(recipientID) }
  }

  override fun selectRejectedSharesForUser(recipientID: UserID): List<DatasetShareListEntry> {
    log.debug("selecting rejected shares for recipient {}", recipientID)
    return connection.use { it.selectRejectedSharesFor(recipientID) }
  }

  override fun selectAllSharesForUser(recipientID: UserID): List<DatasetShareListEntry> {
    log.debug("selecting all shares for recipient {}", recipientID)
    return connection.use { it.selectAllSharesFor(recipientID) }
  }

  override fun selectAllSyncControlRecords(): CloseableIterator<VDIReconcilerTargetRecord> {
    log.debug("selecting all sync control records")
    return connection.selectAllSyncControl()
  }

  override fun selectBrokenDatasetImports(query: BrokenImportListQuery): List<BrokenImportRecord> {
    log.debug("selecting broken dataset import records")
    return connection.use { it.selectBrokenImports(query) }
  }


  override fun openTransaction() =
    CacheDBTransactionImpl(connection.apply { autoCommit = false })

  private fun makeJDBCPostgresConnectionString(host: String, port: UShort, name: String) =
    "jdbc:postgresql://$host:$port/$name"
}

