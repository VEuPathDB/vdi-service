package vdi.core.config.vdi

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.config.raw.common.LDAPConfig
import vdi.config.raw.vdi.InstallTargetConfig
import vdi.config.raw.vdi.PluginConfig
import vdi.core.config.kafka.KafkaConfig
import vdi.core.config.rabbit.RabbitConfig
import vdi.core.config.vdi.daemons.DaemonConfig
import vdi.core.config.vdi.lanes.LaneConfig

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
