package org.veupathdb.service.vdi.db.internal

import org.slf4j.LoggerFactory
import org.veupathdb.service.vdi.db.internal.queries.selectDatasetList
import org.veupathdb.service.vdi.generated.model.DatasetListEntry
import org.veupathdb.service.vdi.model.DatasetListQuery
import javax.sql.DataSource

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