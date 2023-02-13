package vdi.components.client.plugin_handler.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import vdi.components.client.plugin_handler.FieldName

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ImportRequestDetails(

  @JsonProperty(FieldName.VDIID)
  val vdiID: String,

  @JsonProperty(FieldName.Type)
  val type: ImportRequestDetailsType,

  @JsonProperty(FieldName.Projects)
  val projects: Collection<String>,

  @JsonProperty(FieldName.Owner)
  val owner: String,

  @JsonProperty(FieldName.Name)
  val name: String,

  @JsonProperty(FieldName.Summary)
  val summary: String,

  @JsonProperty(FieldName.Description)
  val description: String,

  @JsonProperty(FieldName.Dependencies)
  val dependencies: Collection<ImportRequestDetailsDependency>
)


data class ImportRequestDetailsDependency(
  @JsonProperty(FieldName.ResourceIdentifier)
  val resourceIdentifier: String,

  @JsonProperty(FieldName.ResourceVersion)
  val resourceVersion: String,

  @JsonProperty(FieldName.ResourceDisplayName)
  val resourceDisplayName: String,
)


data class ImportRequestDetailsType(
  @JsonProperty(FieldName.Name)
  val name: String,

  @JsonProperty(FieldName.Version)
  val version: String
)

