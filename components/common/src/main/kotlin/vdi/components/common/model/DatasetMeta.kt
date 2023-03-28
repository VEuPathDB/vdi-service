package vdi.components.common.model

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.components.common.fields.ProjectID
import vdi.components.common.fields.UserID

object DatasetMetaJsonKey {
  const val Owner    = "owner"
  const val Projects = "projects"
  const val Type     = "type"
}

data class DatasetMeta(
  @JsonProperty(DatasetMetaJsonKey.Type)
  var type: DatasetType,

  @JsonProperty(DatasetMetaJsonKey.Projects)
  var projects: Set<ProjectID>,

  @JsonProperty(DatasetMetaJsonKey.Owner)
  var owner: String,
)

object DatasetTypeJsonKey {
  const val Name    = "name"
  const val Version = "version"
}

data class DatasetType(
  @JsonProperty(DatasetTypeJsonKey.Name)
  var name:    String,

  @JsonProperty(DatasetTypeJsonKey.Version)
  var version: String,
)
