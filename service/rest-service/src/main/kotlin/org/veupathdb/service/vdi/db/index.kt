package org.veupathdb.service.vdi.db

import org.veupathdb.lib.container.jaxrs.health.DatabaseDependency
import org.veupathdb.lib.container.jaxrs.health.Dependency
import vdi.component.db.app.AppDatabaseRegistry
import vdi.component.db.cache.CacheDB

fun initDatabaseDependencies(): Array<Dependency> {
  val tmp = HashMap<String, Dependency>(16)

  for ((name, _, ds) in AppDatabaseRegistry) {
    tmp.computeIfAbsent(name) {
      DatabaseDependency(name, ds.host, ds.port.toInt(), ds.source).also { it.setTestQuery("SELECT 1 FROM dual") }
    }
  }

  val cacheDB = CacheDB()

  return ArrayList<Dependency>(tmp.size + 1).also {
    it.addAll(tmp.values)
    it.add(DatabaseDependency(cacheDB.details.name, cacheDB.details.host, cacheDB.details.port.toInt(), cacheDB.dataSource))
  }.toTypedArray()
}
