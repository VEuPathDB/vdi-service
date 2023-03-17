package vdi.component.db.cache

import org.slf4j.LoggerFactory
import vdi.component.db.cache.sql.selectDatasetList
import org.veupathdb.service.vdi.generated.model.DatasetListEntry
import javax.sql.DataSource
import vdi.component.db.cache.model.DatasetListQuery

object CacheDB {
  private val log = LoggerFactory.getLogger(javaClass)

  private lateinit var source: DataSource

  private val connection
    get() = source.connection

  fun init(db: DataSource) {
    source = db
  }

  fun selectDatasetList(query: DatasetListQuery): List<DatasetListEntry> {
    log.debug("selecting dataset list for user {}", query.userID)
    return connection.use { it.selectDatasetList(query) }
  }

  fun openTransaction() =
    CacheDBTransaction(connection.apply { autoCommit = false })
}