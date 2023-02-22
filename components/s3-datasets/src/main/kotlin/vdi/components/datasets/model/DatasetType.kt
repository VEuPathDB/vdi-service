package vdi.components.datasets.model

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetType(@JsonProperty("name") val name: String, @JsonProperty("version") val version: String)