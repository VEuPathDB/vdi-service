package vdi.component.db.cache

import org.slf4j.LoggerFactory
import javax.sql.DataSource
import vdi.component.db.cache.model.DatasetListQuery
import vdi.component.db.cache.model.DatasetRecord
import vdi.component.db.cache.model.DatasetShare
import vdi.component.db.cache.sql.*
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

// TODO: This thing should be a class and not an object, different modules
//  should have their own instances of it.
@Deprecated("use CacheDB")
object OldCacheDB {
  private val log = LoggerFactory.getLogger(javaClass)

  private lateinit var source: DataSource

  private val connection
    get() = source.connection

  fun init(db: DataSource) {
    source = db
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
}

