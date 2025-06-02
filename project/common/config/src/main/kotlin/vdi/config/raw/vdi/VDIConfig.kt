package vdi.config.raw.vdi

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.config.raw.common.LDAPConfig
import vdi.config.raw.kafka.KafkaConfig
import vdi.config.raw.rabbit.RabbitConfig
import vdi.config.raw.vdi.daemons.DaemonConfig
import vdi.config.raw.vdi.lanes.LaneConfig

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
  val plugins: Map<String, PluginConfig>,
  val rabbit: RabbitConfig,
  val siteBuild: String,
  val installTargets: Set<InstallTargetConfig>,
)
