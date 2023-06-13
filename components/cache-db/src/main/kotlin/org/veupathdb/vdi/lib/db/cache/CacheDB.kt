package org.veupathdb.vdi.lib.db.cache

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import org.veupathdb.vdi.lib.db.cache.model.*
import org.veupathdb.vdi.lib.db.cache.sql.select.*
import org.veupathdb.vdi.lib.db.cache.sql.select.selectDataset
import org.veupathdb.vdi.lib.db.cache.sql.select.selectImportMessages
import org.veupathdb.vdi.lib.db.cache.sql.select.selectSharesFor
import org.veupathdb.vdi.lib.db.cache.sql.select.selectSyncControl
import org.veupathdb.vdi.lib.db.cache.sql.update.updateDatasetImportStatus
import javax.sql.DataSource
import org.postgresql.Driver as PostgresDriver

object CacheDB {
  lateinit var details: CacheDBConnectionDetails
    private set

  lateinit var dataSource: DataSource
    private set

  private val log = LoggerFactory.getLogger(javaClass)

  private val connection
    get() = dataSource.connection

  init {
    init(System.getenv())
  }

  internal fun init(env: Environment) {
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

  fun selectDataset(datasetID: DatasetID): DatasetRecord? {
    log.debug("selecting dataset {}", datasetID)
    return connection.use { it.selectDataset(datasetID) }
  }

  fun selectDatasetList(query: DatasetListQuery): List<DatasetRecord> {
    log.debug("selecting dataset list for user {}", query.userID)
    return connection.use { it.selectDatasetList(query) }
  }

  fun selectDatasetForUser(userID: UserID, datasetID: DatasetID): DatasetRecord? {
    log.debug("selecting dataset {} for user {}", datasetID, userID)
    return connection.use { it.selectDatasetForUser(userID, datasetID) }
  }

  fun selectDatasetsForUser(userID: UserID): List<DatasetRecord> {
    log.debug("selecting dataset list for user {}", userID)
    return connection.use { it.selectDatasetsForUser(userID) }
  }

  fun selectSharesForDataset(datasetID: DatasetID): List<DatasetShare> {
    log.debug("selecting shares for dataset {}", datasetID)
    return connection.use { it.selectSharesFor(datasetID) }
  }

  fun selectImportMessages(datasetID: DatasetID): List<String> {
    log.debug("selecting import messages for dataset {}", datasetID)
    return connection.use { it.selectImportMessages(datasetID) }
  }

  fun selectSyncControl(datasetID: DatasetID): VDISyncControlRecord? {
    log.debug("selecting sync control record for dataset {}", datasetID)
    return connection.use { it.selectSyncControl(datasetID) }
  }

  fun selectDeletedDatasets(): List<DeletedDataset> {
    log.debug("selecting deleted datasets")
    return connection.use { it.selectDeletedDatasets() }
  }

  fun updateImportStatus(datasetID: DatasetID, status: DatasetImportStatus) {
    log.debug("updating import status for dataset {} to status {}", datasetID, status)
    return connection.use { it.updateDatasetImportStatus(datasetID, status) }
  }

  fun selectOpenSharesForUser(recipientID: UserID): List<DatasetShareListEntry> {
    log.debug("selecting open shares for recipient {}", recipientID)
    return connection.use { it.selectOpenSharesFor(recipientID) }
  }

  fun selectAcceptedSharesForUser(recipientID: UserID): List<DatasetShareListEntry> {
    log.debug("selecting accepted shares for recipient {}", recipientID)
    return connection.use { it.selectAcceptedSharesFor(recipientID) }
  }

  fun selectRejectedSharesForUser(recipientID: UserID): List<DatasetShareListEntry> {
    log.debug("selecting rejected shares for recipient {}", recipientID)
    return connection.use { it.selectRejectedSharesFor(recipientID) }
  }

  fun selectAllSharesForUser(recipientID: UserID): List<DatasetShareListEntry> {
    log.debug("selecting all shares for recipient {}", recipientID)
    return connection.use { it.selectAllSharesFor(recipientID) }
  }

  /**
   * Streams sorted stream of all dataset control records.
   *
   * @return Stream of dataset control records sorted by user ID and then dataset ID. The stream
   * must be closed to release the db connection.
   */
  fun selectAllSyncControlRecords(): CloseableIterator<Pair<VDIDatasetType, VDISyncControlRecord>> {
    log.debug("selecting all sync control records")
    return connection.selectAllSyncControl()
  }


  fun openTransaction() =
    CacheDBTransaction(connection.apply { autoCommit = false })

  fun withTransaction(fn: (CacheDBTransaction) -> Unit) =
    openTransaction().use(fn)

  private fun makeJDBCPostgresConnectionString(host: String, port: UShort, name: String) =
    "jdbc:postgresql://$host:$port/$name"
}

