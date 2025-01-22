package vdi.component.db.cache

// FIXME: Remove after push to production!
//      : This is a temporary hack because VDI does not yet have an automated
//      : DB update system.  There is an open ticket to add that feature.
//      :
//      : Additionally, remove the dependence on the cache db lib from the
//      : service/bootstrap module.
object TempPatchStableIDs {
  fun applyPatch() {
    CacheDB().dataSource.connection.use { con -> con.createStatement().use { st -> st.execute("ALTER TABLE vdi.datasets ADD COLUMN IF NOT EXISTS user_stable_id VARCHAR UNIQUE") } }
  }
}
