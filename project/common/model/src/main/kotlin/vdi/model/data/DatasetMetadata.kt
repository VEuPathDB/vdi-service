package vdi.model.data

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI
import java.time.OffsetDateTime

@JsonIgnoreProperties("category")
data class DatasetMetadata(
  @field:JsonProperty(JsonKey.Type)
  val type: DatasetType,

  @field:JsonProperty(JsonKey.InstallTargets)
  @field:JsonAlias("projects")
  val installTargets: Set<InstallTargetID>,

  @field:JsonProperty(JsonKey.Visibility)
  val visibility: DatasetVisibility,

  @field:JsonProperty(JsonKey.Owner)
  val owner: UserID,

  @field:JsonProperty(JsonKey.Name)
  val name: String,

  @field:JsonProperty(JsonKey.Summary)
  val summary: String,

  @field:JsonProperty(JsonKey.Origin)
  val origin: String,

  @field:JsonProperty(JsonKey.Created)
  val created: OffsetDateTime,

  @field:JsonProperty(JsonKey.ShortName)
  val shortName: String? = null,

  @field:JsonProperty(JsonKey.Description)
  val description: String? = null,

  @field:JsonProperty(JsonKey.ShortAttribution)
  val shortAttribution: String? = null,

  @field:JsonProperty(JsonKey.SourceURL)
  val sourceURL: URI? = null,

  @field:JsonProperty(JsonKey.Dependencies)
  val dependencies: List<DatasetDependency> = emptyList(),

  @field:JsonProperty(JsonKey.Publications)
  val publications: List<DatasetPublication> = emptyList(),

  @field:JsonProperty(JsonKey.Hyperlinks)
  val hyperlinks: List<DatasetHyperlink> = emptyList(),

  @field:JsonProperty(JsonKey.Organisms)
  val organisms: Set<String> = emptySet(),

  @field:JsonProperty(JsonKey.Contacts)
  val contacts: List<DatasetContact> = emptyList(),

  @field:JsonProperty(JsonKey.OriginalID)
  val originalID: DatasetID? = null,

  @field:JsonProperty(JsonKey.RevisionHistory)
  val revisionHistory: List<DatasetRevision> = emptyList(),

  @field:JsonProperty(JsonKey.Properties)
  val properties: DatasetProperties? = null,

  @field:JsonProperty(JsonKey.Project)
  val project: String? = null,

  @field:JsonProperty(JsonKey.Program)
  val program: String? = null,
) {
  object JsonKey {
    const val Contacts         = "contacts"
    const val Created          = "created"
    const val Dependencies     = "dependencies"
    const val Description      = "description"
    const val Hyperlinks       = "hyperlinks"
    const val InstallTargets   = "installTargets"
    const val Name             = "name"
    const val Organisms        = "organisms"
    const val Origin           = "origin"
    const val OriginalID       = "originalId"
    const val Owner            = "owner"
    const val Program          = "program"
    const val Project          = "project"
    const val Properties       = "properties"
    const val Publications     = "publications"
    const val RevisionHistory  = "revisionHistory"
    const val ShortAttribution = "shortAttribution"
    const val ShortName        = "shortName"
    const val SourceURL        = "sourceUrl"
    const val Summary          = "summary"
    const val Type             = "type"
    const val Visibility       = "visibility"
  }
}
