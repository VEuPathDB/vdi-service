package vdi.lib.config.vdi

import com.fasterxml.jackson.annotation.JsonProperty
import org.veupathdb.vdi.lib.config.LDAPConfig
import vdi.lib.config.vdi.daemons.DaemonConfig
import vdi.lib.config.vdi.lanes.LaneConfig

data class VDIConfig(
  @param:JsonProperty("cacheDb")
  @field:JsonProperty("cacheDb")
  val cacheDB: CacheDBConfig,
  val restService: RestServiceConfig?,
  val daemons: DaemonConfig?,
  val kafka: KafkaConfig,
  val lanes: LaneConfig?,
  val ldap: LDAPConfig?,
  val objectStore: ObjectStoreConfig,
  val plugins: Set<PluginConfig>,
  val rabbit: RabbitConfigs,
  val siteBuild: String,
  val installTargets: Set<InstallTargetConfig>,
)
