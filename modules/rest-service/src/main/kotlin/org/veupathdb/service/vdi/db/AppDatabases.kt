package org.veupathdb.service.vdi.db

import javax.sql.DataSource

object AppDatabases {
  private val dataSources = HashMap<String, DataSource>(12)

  operator fun contains(key: String): Boolean = dataSources.containsKey(key)

  operator fun get(key: String): DataSource? = dataSources[key]

  operator fun set(key: String, ds: DataSource) { dataSources[key] = ds }
}