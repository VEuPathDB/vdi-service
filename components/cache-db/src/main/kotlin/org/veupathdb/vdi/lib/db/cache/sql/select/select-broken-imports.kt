package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.db.cache.model.BrokenImportListQuery
import org.veupathdb.vdi.lib.db.cache.model.BrokenImportRecord
import org.veupathdb.vdi.lib.db.cache.util.*
import org.veupathdb.vdi.lib.db.cache.util.getDatasetID
import org.veupathdb.vdi.lib.db.cache.util.getUserID
import org.veupathdb.vdi.lib.db.cache.util.withPreparedStatement
import org.veupathdb.vdi.lib.db.cache.util.withResults
import java.sql.Connection
import java.sql.SQLType
import java.sql.Types

// language=postgresql
private fun sqlBody(
  userIDFilter: String,
  beforeFilter: String,
  afterFilter:  String,
  orderBy:      String,
  sortOrder:    String,
  limit:        UByte,
  offset:       UInt,
) = """
SELECT
  d.dataset_id
, d.type_name
, d.type_version
, d.owner_id
, array(SELECT p.project_id FROM vdi.dataset_projects AS p WHERE p.dataset_id = d.dataset_id) AS projects
FROM
  vdi.datasets AS d
WHERE
  1 = 1
  $userIDFilter
  $beforeFilter
  $afterFilter
ORDER BY
  $orderBy $sortOrder
LIMIT
  $limit
OFFSET
  $offset
"""

internal fun Connection.selectBrokenImports(query: BrokenImportListQuery): List<BrokenImportRecord> {
  val params = ArrayList<Pair<Any, Int>>(3)

  val userIDFilter: String
  val beforeFilter: String
  val afterFilter: String

  if (query.hasUserID) {
    userIDFilter = "AND d.owner_id = ?"
    params.add(query.userID!!.toString() to Types.VARCHAR)
  } else {
    userIDFilter = ""
  }

  if (query.hasBefore) {
    beforeFilter = "AND d.created < ?"
    params.add(query.before!! to Types.TIMESTAMP_WITH_TIMEZONE)
  } else {
    beforeFilter = ""
  }

  if (query.hasAfter) {
    afterFilter = "AND d.created > ?"
    params.add(query.after!! to Types.TIMESTAMP_WITH_TIMEZONE)
  } else {
    afterFilter = ""
  }

  val sql = sqlBody(
    userIDFilter = userIDFilter,
    beforeFilter = beforeFilter,
    afterFilter  = afterFilter,
    orderBy      = query.sortBy.toString(),
    sortOrder    = query.order.toString(),
    limit        = query.limit,
    offset       = query.offset
  )

  return withPreparedStatement(sql) {
    params.forEachIndexed { i, pair ->
      setObject(i, pair.first, pair.second)
    }

    withResults {
      val out = ArrayList<BrokenImportRecord>(query.limit.toInt())

      while (next())
        out.add(BrokenImportRecord(
          getDatasetID("dataset_id"),
          getUserID("owner_id"),
          getString("type_name"),
          getString("type_version"),
          getProjectIDList("projects")
        ))

      out
    }
  }
}