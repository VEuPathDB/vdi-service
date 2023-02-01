package vdi.service.model

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetMeta(
  @JsonProperty("type")
  val type: DatasetType,

  @JsonProperty("projects")
  val projects: Collection<String>,

  @JsonProperty("owner")
  val owner: String, // TODO: Should this be a string or a number?

  @JsonProperty("name")
  val name: String,

  @JsonProperty("summary")
  val summary: String,

  @JsonProperty("description")
  val description: String,

  @JsonProperty("dependencies")
  val dependencies: Collection<DatasetDependency>,
)

