package org.veupathdb.service.vdi

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.veupathdb.lib.container.jaxrs.config.Options
import org.veupathdb.lib.container.jaxrs.health.DatabaseDependency
import org.veupathdb.lib.container.jaxrs.health.Dependency
import org.veupathdb.lib.container.jaxrs.server.Server
import org.veupathdb.lib.ldap.LDAP
import org.veupathdb.lib.ldap.LDAPConfig
import org.veupathdb.service.vdi.db.AppDatabases
import org.veupathdb.service.vdi.config.Options as Opts

object Main : Server() {
  val LDAP = LDAP(LDAPConfig(Opts.LDAP.ldapServers, Opts.LDAP.oracleBaseDN))

  @JvmStatic
  fun main(args: Array<String>) {
    enableAccountDB()
    start(args)
  }

  override fun newResourceConfig(opts: Options) = Resources(opts as Opts)

  override fun dependencies(): Array<Dependency> {
    val out = ArrayList<Dependency>(12)

    for (db in Opts.AppDatabases) {
      val ld = LDAP.requireSingularOracleNetDesc(db.ldap)
      val ds = makeJDBCOracleConnectionString(ld.host, ld.port, ld.serviceName)
        .let { jdbcString -> HikariConfig().apply {
          jdbcUrl = jdbcString
          username = db.username
          password = db.password
          maximumPoolSize = db.poolSize
        } }
        .let { HikariDataSource(it) }

      out.add(DatabaseDependency(db.name, ld.host, ld.port.toInt(), ds))

      AppDatabases[db.name] = ds
    }

    return out.toTypedArray()
  }

  override fun newOptions() = Opts
}

private fun makeJDBCOracleConnectionString(host: String, port: UShort, name: String) =
  "jdbc:oracle:thin:@//$host:$port/$name"