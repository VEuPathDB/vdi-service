package vdi.db.app

import org.veupathdb.lib.ldap.LDAP
import org.veupathdb.lib.ldap.LDAPConfig
import org.veupathdb.lib.ldap.LDAPHost
import vdi.config.loadAndCacheStackConfig
import vdi.config.raw.db.DatabaseConnectionConfig
import vdi.config.raw.db.DirectDatabaseConnectionConfig
import vdi.config.raw.db.LDAPDatabaseConnectionConfig
import vdi.logging.MetaLogger
import vdi.model.data.DataType
import vdi.model.data.DatasetType
import vdi.model.data.InstallTargetID
import vdi.model.field.HostAddress

object InstallTargetRegistry {
  private val registry: Map<InstallTargetID, InstallTargetInstanceRegistry>

  init {
    val config = loadAndCacheStackConfig().vdi
    val ldap = config.ldap?.let { raw -> LDAP(LDAPConfig(
      hosts  = raw.servers.map { LDAPHost(it.host, it.port ?: 389u) },
      baseDN = raw.baseDN
    )) }

    registry = config.installTargets.asSequence()
      .filter { it.enabled.also { enabled -> when {
        enabled -> MetaLogger.info("install target {} is marked as enabled", it.targetName)
        else    -> MetaLogger.warn("install target {} is marked as disabled", it.targetName)
      } } }
      .map { c ->
        InstallTarget(
          name            = c.targetName,
          installDatabase = c.dataDB.resolve(ldap),
          controlDatabase = c.controlDB.resolve(ldap),
          propertySchema  = c.datasetPropertySchema.resolve(),
        ).let { tgt ->
          c.targetName to InstallTargetInstanceRegistry(registry = when {
            c.dataTypes.isEmpty() -> mapOf(FallbackDatasetType to tgt)
            else                  -> c.dataTypes.associateWith { tgt }
          })
        }
      }
      .toMap()
  }

  operator fun get(installTarget: InstallTargetID) =
    registry[installTarget]?.asSequence() ?: emptySequence()

  operator fun get(installTarget: InstallTargetID, dataType: DataType) =
    registry[installTarget]?.get(dataType) ?: emptySequence()

  operator fun get(installTarget: InstallTargetID, dataType: DatasetType) =
    registry[installTarget]?.get(dataType)

  fun asSequence() =
    registry.asSequence()
      .flatMap { (id, reg) -> reg.asSequence().map { (type, target) -> Triple(id, type, target) } }
}
