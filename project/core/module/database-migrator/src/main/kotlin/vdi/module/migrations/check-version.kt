package vdi.module.migrations

import io.foxcapades.kdbc.withStatementResults
import java.sql.Connection

// language=postgresql
private const val VERSION_SQL = "SELECT value FROM vdi.service_metadata WHERE key = 'db.version'"

// language=postgresql
private const val TABLE_SQL = """
SELECT
  1
FROM
  information_schema.tables
WHERE
  table_schema = 'vdi'
  AND table_name = 'service_metadata'
"""

internal fun Connection.checkVersion(): Int =
  withStatementResults(TABLE_SQL) {
    if (next())
      withStatementResults(VERSION_SQL) { if (next()) getInt(1) else 0 }
    else
      0
  }

