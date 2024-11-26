package vdi.component.db.cache.sql.select

import vdi.component.db.cache.model.BrokenImportListQuery
import vdi.component.db.cache.model.BrokenImportRecord
import vdi.component.db.cache.util.*
import java.sql.Connection
import java.sql.Types

private fun sqlBody(
  userIDFilter: String,
  beforeFilter: String,
  afterFilter:  String,
  orderBy:      String,
  sortOrder:    String,
  limit:        UByte,
  offset:       UInt,
) =
// language=postgresql
"""
SELECT
  d.dataset_id
, d.type_name
, d.type_version
, d.owner_id
, array(SELECT p.project_id FROM vdi.dataset_projects AS p WHERE p.dataset_id = d.dataset_id) AS projects
, array(SELECT m.message FROM vdi.import_messages AS m WHERE m.dataset_id = d.dataset_id) AS messages
FROM
  vdi.datasets AS d
  INNER JOIN vdi.import_control AS i
    USING (dataset_id)
WHERE
  i.status = 'failed'
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

  val orderBy = when (query.sortBy) {
    BrokenImportListQuery.SortField.Date -> "d.created"
  }

  val sql = sqlBody(
    userIDFilter = userIDFilter,
    beforeFilter = beforeFilter,
    afterFilter  = afterFilter,
    orderBy      = orderBy,
    sortOrder    = query.order.toString(),
    limit        = query.limit,
    offset       = query.offset
  )

  return withPreparedStatement(sql) {
    params.forEachIndexed { i, pair ->
      setObject(i+1, pair.first, pair.second)
    }

    withResults {
      val out = ArrayList<BrokenImportRecord>(query.limit.toInt())

      while (next())
        out.add(BrokenImportRecord(
          datasetID   = getDatasetID("dataset_id"),
          ownerID     = getUserID("owner_id"),
          typeName    = getDataType("type_name"),
          typeVersion = getString("type_version"),
          projects    = getProjectIDList("projects"),
          messages    = getStringList("messages"),
        ))

      out
    }
  }
}
