package org.veupathdb.service.vdi

import org.veupathdb.lib.container.jaxrs.config.Options
import org.veupathdb.lib.container.jaxrs.server.Server
import org.veupathdb.lib.ldap.LDAP
import org.veupathdb.lib.ldap.LDAPConfig
import org.veupathdb.service.vdi.db.initDatabaseConnections
import org.veupathdb.service.vdi.config.Options as Opts

object RestService : Server() {
  val LDAP = LDAP(LDAPConfig(Opts.LDAP.ldapServers, Opts.LDAP.oracleBaseDN))

  @JvmStatic
  fun main(args: Array<String>) {
    enableAccountDB()
    start(args)
  }

  override fun newResourceConfig(opts: Options) = Resources(opts as Opts).apply {
    property("jersey.config.server.tracing.type", "ALL")
    property("jersey.config.server.tracing.threshold", "VERBOSE")
  }

  override fun dependencies() = initDatabaseConnections(LDAP)

  override fun newOptions() = Opts
}

