package vdi.components.datasets.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DatasetMeta(
  @JsonProperty("type")
  val type: DatasetType,

  @JsonProperty("projects")
  val projects: Collection<String>,

  @JsonProperty("owner")
  val owner: String,

  @JsonProperty("name")
  val name: String,

  @JsonProperty("summary")
  val summary: String?,

  @JsonProperty("description")
  val description: String?,

  @JsonProperty("dependencies")
  val dependencies: Collection<DatasetDependency>
)
