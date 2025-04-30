package vdi.lib.db.cache

// FIXME: REMOVE THIS ONCE THE NEW FEATURES HAVE BEEN APPLIED TO PRODUCTION!!!!
fun patchMetadataTable() {
  CacheDB().dataSource.connection.use { con ->
    con.createStatement().use { stmt ->
      // Add new searchable metadata fields
      // language=postgresql
      stmt.execute("""
        ALTER TABLE vdi.dataset_metadata
          ADD COLUMN IF NOT EXISTS short_name VARCHAR
        , ADD COLUMN IF NOT EXISTS short_attribution VARCHAR
        , ADD COLUMN IF NOT EXISTS category VARCHAR
      """.trimIndent())

      // Add new table for dataset data revisioning
      // language=postgresql
      stmt.execute("""
        CREATE TABLE IF NOT EXISTS vdi.dataset_revisions (
          revision_id VARCHAR(12) PRIMARY KEY
        , original_id VARCHAR(12) NOT NULL
        , action      SMALLINT    NOT NULL
        , timestamp   TIMESTAMPTZ NOT NULL
        , CONSTRAINT revision_dedupe UNIQUE (revision_id, original_id)
        )
      """.trimIndent())

      // Add index to data revisioning table
      // language=postgresql
      stmt.execute("CREATE INDEX IF NOT EXISTS dataset_revisions_original_id ON vdi.dataset_revisions (original_id)")
    }
  }
}
