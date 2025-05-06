package vdi.lib.ldap

import org.veupathdb.lib.ldap.LDAP
import org.veupathdb.lib.ldap.LDAPConfig
import org.veupathdb.lib.ldap.LDAPHost
import vdi.lib.config.loadAndCacheStackConfig

object LDAP {
  private val ldap: LDAP? = loadAndCacheStackConfig().vdi.ldap?.let {
    LDAP(LDAPConfig(
      it.servers.map { (host, port) -> LDAPHost(host, port ?: 389u) },
      it.baseDN,
    ))
  }

  private inline val reqLDAP
    get() = ldap ?: throw IllegalStateException("ldap is not configured")

  fun requireSingularOracleNetDesc(commonName: String) = reqLDAP.requireSingularOracleNetDesc(commonName)

  fun lookupOracleNetDesc(commonName: String) = reqLDAP.lookupOracleNetDesc(commonName)
}
