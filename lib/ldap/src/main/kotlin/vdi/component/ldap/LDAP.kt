package vdi.component.ldap

import org.veupathdb.lib.ldap.LDAP
import org.veupathdb.lib.ldap.LDAPConfig
import org.veupathdb.lib.ldap.LDAPHost
import org.veupathdb.vdi.lib.common.env.EnvKey
import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.reqHostAddresses
import org.veupathdb.vdi.lib.common.env.require

object LDAP {

  private lateinit var ldap: LDAP

  init {
    init(System.getenv())
  }

  fun requireSingularOracleNetDesc(commonName: String) = ldap.requireSingularOracleNetDesc(commonName)

  fun lookupOracleNetDesc(commonName: String) = ldap.lookupOracleNetDesc(commonName)

  private fun init(env: Environment) {
    val servers = env.reqHostAddresses(EnvKey.LDAP.LDAPServers)
      .asSequence()
      .map { (host, port) -> LDAPHost(host, port) }
      .toList()
    val oracleDN = env.require(EnvKey.LDAP.OracleBaseDN)

    ldap = LDAP(LDAPConfig(servers, oracleDN))
  }
}