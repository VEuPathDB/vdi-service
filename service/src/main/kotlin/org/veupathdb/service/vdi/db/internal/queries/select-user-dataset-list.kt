package org.veupathdb.service.vdi.db.internal.queries

import org.veupathdb.service.vdi.model.DatasetListQuery
import java.sql.Connection

// language=postgresql
private const val SQL_PREFIX = """
SELECT
FROM
"""

fun Connection.selectUserDatasetList(query: DatasetListQuery) {

}