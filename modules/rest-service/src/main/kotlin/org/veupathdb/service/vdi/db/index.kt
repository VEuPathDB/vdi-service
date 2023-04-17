package org.veupathdb.service.vdi.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.health.DatabaseDependency
import org.veupathdb.lib.container.jaxrs.health.Dependency
import org.veupathdb.lib.ldap.LDAP
import org.veupathdb.service.vdi.config.Options
import org.veupathdb.vdi.lib.db.app.AppDatabaseRegistry
import org.postgresql.Driver as PostgresDriver


fun initDatabaseConnections(ldap: LDAP): Array<Dependency> {
  val log = LoggerFactory.getLogger("db")
  log.trace("initDatabaseConnections(ldap = {})", ldap)

  val out = ArrayList<Dependency>(13)

  out.addAll(initAppDBConnections(ldap, log))
  out.add(initCacheDBConnection(log))

  return out.toTypedArray()
}

private fun initCacheDBConnection(log: Logger): Dependency {
  log.trace("initCacheDBConnection(log = {})", log)

  log.debug("initializing cache db connection")
  val ds = HikariConfig()
    .apply {
      jdbcUrl = makeJDBCPostgresConnectionString(
        Options.CacheDB.host,
        Options.CacheDB.port,
        Options.CacheDB.name
      )
      username = Options.CacheDB.username
      password = Options.CacheDB.password
      maximumPoolSize = Options.CacheDB.poolSize.toInt()
      driverClassName =  PostgresDriver::class.java.name
    }
    .let(::HikariDataSource)

  val dd = DatabaseDependency("cache-db", Options.CacheDB.host, Options.CacheDB.port.toInt(), ds)
  dd.setTestQuery("SELECT 1")

  return dd
}

private fun initAppDBConnections(ldap: LDAP, log: Logger): List<Dependency> {
  log.trace("initAppDBConnections(ldap = {}, log = {})", ldap, log)

  val out = ArrayList<Dependency>(12)

  for ((name, ds) in AppDatabaseRegistry) {
    val dd = DatabaseDependency(name, ds.host, ds.port.toInt(), ds.source)
    dd.setTestQuery("SELECT 1 FROM dual")
    out.add(dd)
  }

  return out
}

private fun makeJDBCOracleConnectionString(host: String, port: UShort, name: String) =
  "jdbc:oracle:thin:@//$host:$port/$name"

private fun makeJDBCPostgresConnectionString(host: String, port: UShort, name: String) =
  "jdbc:postgresql://$host:$port/$name"