package vdi.service.plugin.conf

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PluginConfig(val vdi: ShortVDIConfig)

