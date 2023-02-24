package org.veupathdb.service.vdi.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.veupathdb.lib.container.jaxrs.health.DatabaseDependency
import org.veupathdb.lib.container.jaxrs.health.Dependency
import org.veupathdb.lib.ldap.LDAP
import org.veupathdb.service.vdi.Main
import org.veupathdb.service.vdi.config.Options

fun initDatabaseConnections(ldap: LDAP): Array<Dependency> {
  val out = ArrayList<Dependency>(12)

  for (db in Options.AppDatabases) {
    val ld = Main.LDAP.requireSingularOracleNetDesc(db.ldap)
    val ds = makeJDBCOracleConnectionString(ld.host, ld.port, ld.serviceName)
      .let { jdbcString -> HikariConfig().apply {
        jdbcUrl = jdbcString
        username = db.username
        password = db.password
        maximumPoolSize = db.poolSize
      } }
      .let { HikariDataSource(it) }

    val dd = DatabaseDependency(db.name, ld.host, ld.port.toInt(), ds)
    dd.setTestQuery("SELECT 1 FROM dual")

    out.add(dd)

    AppDatabases[db.name] = ds
  }

  return out.toTypedArray()

}

private fun makeJDBCOracleConnectionString(host: String, port: UShort, name: String) =
  "jdbc:oracle:thin:@//$host:$port/$name"