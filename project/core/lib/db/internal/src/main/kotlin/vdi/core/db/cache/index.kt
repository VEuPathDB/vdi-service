@file:JvmName("CacheDbProvider")
package vdi.core.db.cache

/**
 * Returns the internal cache database accessor instance.
 */
@JvmName("cacheDb")
fun CacheDB(): CacheDB = CacheDBImpl
