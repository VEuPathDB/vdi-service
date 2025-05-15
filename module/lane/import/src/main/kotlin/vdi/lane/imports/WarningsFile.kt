package vdi.lane.imports

import com.fasterxml.jackson.annotation.JsonProperty

internal data class WarningsFile(@JsonProperty("warnings") val warnings: List<String>)
