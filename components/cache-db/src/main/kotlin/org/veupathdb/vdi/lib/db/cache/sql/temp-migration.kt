package org.veupathdb.vdi.lib.db.cache.sql

import org.slf4j.LoggerFactory
import java.sql.Connection

// 1. Test whether the `inserted` column exists yet.
//
// language=postgresql
private const val TestQuery = """
SELECT
  count(1)
FROM
  information_schema.columns
WHERE
  table_schema = 'vdi'
  AND table_name = 'datasets'
  AND column_name = 'inserted'
"""

// 2. Add the column without the non-null constraint
//
// language=postgresql
private const val AddColumn = """
ALTER TABLE vdi.datasets
  ADD COLUMN inserted TIMESTAMP WITH TIME ZONE;
"""

// 3. Populate new column with the value of the previous creation date column.
//
// language=postgresql
private const val PopulateColumn = """
UPDATE
  vdi.datasets
SET
  inserted = created
"""

// 4. Lock the column to prevent invalid rows
//
// language=postgresql
private const val LockColumn = """
ALTER TABLE vdi.datasets
  ALTER COLUMN inserted SET NOT NULL
"""

private val Log = LoggerFactory.getLogger("temp-migration.kt")

internal fun Connection.migrateDatabase() {
  if (needToMigrate()) {
    createColumn()
    populateColumn()
    lockColumn()
  }
}

private fun Connection.needToMigrate(): Boolean {
  Log.info("testing whether the database migration is needed")
  createStatement().use { stmt ->
    stmt.executeQuery(TestQuery).use { rs ->
      rs.next()
      return rs.getInt(1) == 0
    }
  }
}

private fun Connection.createColumn() {
  Log.info("creating dataset insertion date column")
  createStatement().use { stmt -> stmt.execute(AddColumn) }
}

private fun Connection.populateColumn() {
  Log.info("populating dataset insertion date column")
  createStatement().use { stmt -> stmt.execute(PopulateColumn) }
}

private fun Connection.lockColumn() {
  Log.info("locking dataset insertion date column")
  createStatement().use { stmt -> stmt.execute(LockColumn) }
}