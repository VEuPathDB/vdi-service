package vdi.core.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import vdi.core.config.core.ContainerCoreConfig
import vdi.core.config.vdi.VDIConfig

@JsonIgnoreProperties($$"$schema", "definitions")
data class StackConfig(
  @param:JsonProperty("containerCore")
  @field:JsonProperty("containerCore")
  val core: ContainerCoreConfig,

  val vdi: VDIConfig,
)
