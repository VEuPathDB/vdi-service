package vdi.db.app

import org.veupathdb.lib.ldap.LDAP
import vdi.config.raw.db.DatabaseConnectionConfig
import vdi.config.raw.db.DirectDatabaseConnectionConfig
import vdi.config.raw.db.LDAPDatabaseConnectionConfig
import vdi.model.data.DataType
import vdi.model.data.DatasetType
import vdi.model.field.HostAddress

val FallbackDatasetType = DatasetType(DataType.of("*"), "*")

inline val DatasetType.isFallback get() = this == FallbackDatasetType

inline val TargetDBPlatform.dbiName get() = name

internal fun DatabaseConnectionConfig.resolve(ldap: LDAP?): TargetDatabaseDetails {
  return when (this) {
    is DirectDatabaseConnectionConfig -> TargetDBPlatform.fromString(platform)
      .let { platform -> TargetDatabaseDetails(
        name = dbName,
        server = server.toHostAddress(platform.defaultPort),
        user = username,
        pass = password,
        schema = schema ?: username,
        poolSize = poolSize ?: 5u,
        platform = platform,
      ) }

    is LDAPDatabaseConnectionConfig   -> {
      val desc = ldap!!.requireSingularNetDesc(lookupCN)
      val platform = TargetDBPlatform.fromString(desc.platform.name)

      TargetDatabaseDetails(
        name = desc.identifier,
        server = HostAddress(desc.host, desc.port.toUShort()),
        user = username,
        pass = password,
        schema = schema ?: username,
        poolSize = poolSize ?: 5u,
        platform = platform
      )
    }
  }
}