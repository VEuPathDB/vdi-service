package vdi.core.ldap

import org.veupathdb.lib.ldap.LDAP
import org.veupathdb.lib.ldap.LDAPConfig
import org.veupathdb.lib.ldap.LDAPHost
import vdi.config.loadAndCacheStackConfig

object LDAP {
  private val ldap: LDAP? = loadAndCacheStackConfig().vdi.ldap?.let {
    LDAP(LDAPConfig(
      it.servers.map { (host, port) -> LDAPHost(host, port ?: 389u) },
      it.baseDN,
    ))
  }

  private inline val reqLDAP
    get() = ldap ?: throw IllegalStateException("ldap is not configured")

  fun requireSingularNetDesc(commonName: String) = reqLDAP.requireSingularNetDesc(commonName)

  fun lookupNetDesc(commonName: String) = reqLDAP.lookupNetDesc(commonName)
}
