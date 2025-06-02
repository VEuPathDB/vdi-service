package vdi.config.raw

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import vdi.config.raw.core.ContainerCoreConfig
import vdi.config.raw.vdi.VDIConfig

@JsonIgnoreProperties("\$schema", "definitions")
data class StackConfig(
  @param:JsonProperty("containerCore")
  @field:JsonProperty("containerCore")
  val core: ContainerCoreConfig,

  val vdi: VDIConfig,
)
