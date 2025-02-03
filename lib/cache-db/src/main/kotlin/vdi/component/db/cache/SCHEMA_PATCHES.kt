package vdi.component.db.cache

// FIXME: REMOVE THIS ONCE THE EXTENDED METADATA PATCH HAS BEEN APPLIED TO PRODUCTION!!!!
fun patchMetadataTable() {
  CacheDB().dataSource.connection.use { con ->
    con.createStatement().use { stmt ->
      // language=postgresql
      stmt.execute("""
        ALTER TABLE vdi.dataset_metadata
          ADD COLUMN IF NOT EXISTS short_name VARCHAR
        , ADD COLUMN IF NOT EXISTS short_attribution VARCHAR
        , ADD COLUMN IF NOT EXISTS category VARCHAR
      """.trimIndent())
    }
  }
}
