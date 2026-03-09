@file:JvmName("CacheDBManager")
package vdi.core.db.cache

/**
 * Returns the internal cache database accessor instance.
 */
@JvmName("getInstance")
fun CacheDB(): CacheDB = CacheDBImpl
