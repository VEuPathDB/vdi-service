package vdi.service.rest.config

import org.gusdb.fgputil.db.platform.SupportedPlatform
import org.veupathdb.lib.container.jaxrs.config.DbOptions
import org.veupathdb.lib.container.jaxrs.config.DbOptionsImpl
import org.veupathdb.lib.container.jaxrs.config.Options
import java.util.Optional
import vdi.config.raw.ManifestConfig
import vdi.core.config.StackConfig
import vdi.config.raw.db.DatabaseConnectionConfig
import vdi.config.raw.db.DirectDatabaseConnectionConfig
import vdi.config.raw.db.LDAPDatabaseConnectionConfig

class ServiceConfig(config: StackConfig, val manifestConfig: ManifestConfig) : Options() {
  private val coreConfig = config.core

  val uploads = UploadConfig(config.vdi.restService)

  val enableTrace = config.vdi.restService?.enableJerseyTrace ?: false

  val projects = config.vdi.installTargets
    .map { it.targetName }

  override fun getAdminAuthToken() =
    Optional.of(coreConfig
      .authentication
      .adminToken
      .asString)

  override fun getServerPort() =
    Optional.ofNullable(coreConfig.http?.bindPort?.toInt() ?: 80)

  override fun getCorsEnabled() =
    coreConfig.http?.enableCORS ?: false

  override fun getOAuthUrl() =
    Optional.of(coreConfig.authentication.oauth.url)

  override fun getOAuthClientId() =
    Optional.of(coreConfig.authentication.oauth.clientID)

  override fun getOAuthClientSecret() =
    Optional.of(coreConfig.authentication.oauth.clientSecret.asString)

  override fun getKeyStoreFile() =
    Optional.ofNullable(coreConfig.authentication.oauth.keystoreFile)

  override fun getKeyStorePassPhrase() =
    Optional.ofNullable(coreConfig.authentication.oauth.keystorePass?.asString)

  override fun getAppDbOpts() =
    coreConfig.databases?.applicationDB.toDbOptions()

  override fun getAcctDbOpts() =
    coreConfig.databases?.accountDB.toDbOptions()

  override fun getUserDbOpts() =
    coreConfig.databases?.userDB.toDbOptions()

  override fun getUserDbSchema(): String =
    coreConfig.databases?.userDB?.schema ?: super.getUserDbSchema()

  override fun getLdapServers() =
    Optional.ofNullable(coreConfig.ldap?.servers?.joinToString(",") { "${it.host}:${it.port ?: 389}" })

  override fun getDbLookupBaseDn() =
    Optional.ofNullable(coreConfig.ldap?.baseDN)

  private fun DatabaseConnectionConfig?.toDbOptions(): DbOptions =
    when (this) {
      null                              -> throw IllegalStateException("app db is not configured")
      is LDAPDatabaseConnectionConfig   -> DbOptionsImpl(
        lookupCN,
        null, // host
        null, // port
        null, // db name
        username,
        password.asString,
        null, // platform
        poolSize?.toInt(),
        "app-db",
      )
      is DirectDatabaseConnectionConfig -> DbOptionsImpl(
        null, // lookup cn
        server.host,
        server.port?.toInt(),
        dbName,
        username,
        password.asString,
        SupportedPlatform.toPlatform(platform),
        poolSize?.toInt(),
        "app-db"
      )
    }
}
