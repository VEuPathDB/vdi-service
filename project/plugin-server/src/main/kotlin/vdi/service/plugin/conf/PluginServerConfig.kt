package vdi.service.plugin.conf

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PluginServerConfig(val vdi: ShortVDIConfig)

