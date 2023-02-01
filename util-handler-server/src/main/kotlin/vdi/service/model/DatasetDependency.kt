package vdi.service.model

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetDependency(
  @JsonProperty("resourceIdentifier")
  val resourceIdentifier: String,

  @JsonProperty("resourceVersion")
  val resourceVersion: String,

  @JsonProperty("resourceDisplayName")
  val resourceDisplayName: String,
)