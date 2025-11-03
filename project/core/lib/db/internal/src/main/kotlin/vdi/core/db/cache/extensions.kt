package vdi.core.db.cache

inline fun <T> CacheDB.withTransaction(fn: (CacheDBTransaction) -> T) =
  openTransaction().use { t ->
    try {
      fn(t).also { t.commit() }
    } catch (e: Throwable) {
      t.rollback()
      throw e
    }
  }
