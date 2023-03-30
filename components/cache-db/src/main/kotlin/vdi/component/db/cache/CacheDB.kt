package vdi.component.db.cache

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.postgresql.Driver
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import javax.sql.DataSource
import vdi.component.db.cache.model.DatasetListQuery
import vdi.component.db.cache.model.DatasetRecord
import vdi.component.db.cache.model.DatasetShare
import vdi.component.db.cache.sql.*
import vdi.component.db.cache.sql.selectDataset
import vdi.component.db.cache.sql.selectDatasetForUser
import vdi.component.db.cache.sql.selectSharesFor

class CacheDB(private val dataSource: DataSource) {
  private val log = LoggerFactory.getLogger(javaClass)

  private val connection
    get() = dataSource.connection

  constructor(config: CacheDBConfig) : this(setupDataSource(config))

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

  fun selectSharesForDataset(datasetID: DatasetID): List<DatasetShare> {
    log.debug("selecting shares for dataset {}", datasetID)
    return connection.use { it.selectSharesFor(datasetID) }
  }

  fun selectImportMessages(datasetID: DatasetID): List<String> {
    log.debug("selecting import messages for dataset {}", datasetID)
    return connection.use { it.selectImportMessages(datasetID) }
  }

  fun openTransaction() =
    CacheDBTransaction(connection.apply { autoCommit = false })

  private companion object {
    @JvmStatic
    private fun setupDataSource(config: CacheDBConfig): DataSource {
      return HikariConfig()
        .apply {
          jdbcUrl = makeJDBCPostgresConnectionString(config)
          username = config.username.unwrap()
          password = config.password.unwrap()
          maximumPoolSize = config.poolSize.toInt()
          driverClassName =  Driver::class.java.name
        }
        .let(::HikariDataSource)
    }

    @JvmStatic
    private fun makeJDBCPostgresConnectionString(config: CacheDBConfig) =
      "jdbc:postgresql://${config.host}:${config.port}/${config.name}"
  }

}