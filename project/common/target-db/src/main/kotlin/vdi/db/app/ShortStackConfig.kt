package vdi.db.app

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class ShortStackConfig(val vdi: ShortVDIConfig)