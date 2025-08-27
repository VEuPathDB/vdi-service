package vdi.module.migrations

import io.foxcapades.kdbc.withStatementResults
import java.sql.Connection

// language=postgresql
private const val SQL = "SELECT value FROM vdi.service_metadata WHERE key = 'db.version'"

internal fun Connection.checkVersion(): Int =
  withStatementResults(SQL) { if (next()) getInt(1) else 0 }
