package vdi.components.datasets.model

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetDependency(
  @JsonProperty("resourceIdentifier")
  val identifier: String,

  @JsonProperty("resourceVersion")
  val version: String,

  @JsonProperty("resourceDisplayName")
  val displayName: String,
)