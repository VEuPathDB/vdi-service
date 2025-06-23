package vdi.core.db.cache

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
      """.trimIndent())

      // Add new table for dataset data revisioning
      // language=postgresql
      stmt.execute("""
        CREATE TABLE IF NOT EXISTS vdi.dataset_revisions (
          revision_id VARCHAR(16) PRIMARY KEY -- new dataset ID
        , original_id VARCHAR(16) NOT NULL    -- ORIGINAL dataset ID (not the immediate parent)
        , action      SMALLINT    NOT NULL    -- Action enum ID
        , timestamp   TIMESTAMPTZ NOT NULL    -- timestamp the revision was created
        )
      """.trimIndent())

      // Add index to data revisioning table
      // language=postgresql
      stmt.execute("CREATE INDEX IF NOT EXISTS dataset_revisions_original_id ON vdi.dataset_revisions (original_id)")

      // language=postgresql
      stmt.execute("""
        ALTER TABLE vdi.dataset_revisions
          ALTER COLUMN revision_id TYPE varchar(16)
        , ALTER COLUMN original_id TYPE varchar(16)
      """.trimIndent())
    }
  }
}
