package vdi.lib.config.vdi

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.lib.config.common.LDAPConfig
import vdi.lib.config.vdi.daemons.DaemonConfig
import vdi.lib.config.vdi.lanes.LaneConfig

data class VDIConfig(
  @JsonProperty("cacheDb")
  val cacheDB: CacheDBConfig,
  val daemons: DaemonConfig?,
  val kafka: KafkaConfig,
  val lanes: LaneConfig?,
  val ldap: LDAPConfig,
  val objectStore: ObjectStoreConfig,
  val plugins: Set<PluginConfig>,
  val rabbit: RabbitConfigs,
  val siteBuild: String,
  @JsonProperty("targetDbs")
  val targetDBs: Set<InstallTargetConfigBase>,
)
