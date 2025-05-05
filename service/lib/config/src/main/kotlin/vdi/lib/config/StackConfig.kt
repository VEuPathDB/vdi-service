package vdi.lib.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import vdi.lib.config.core.ContainerCoreConfig
import vdi.lib.config.vdi.VDIConfig

@JsonIgnoreProperties("\$schema", "definitions")
data class StackConfig(
  @JsonProperty("containerCore")
  val core: ContainerCoreConfig?,
  @JsonProperty("vdiConfig")
  val vdi: VDIConfig,
)
