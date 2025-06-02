package vdi.model.data

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class DatasetMetadata(
  @JsonProperty(JsonKey.Type)
  val type: DatasetType,

  @JsonProperty(JsonKey.InstallTargets)
  @JsonAlias(JsonKey.Projects)
  val installTargets: Set<InstallTargetID>,

  @JsonProperty(JsonKey.Visibility)
  val visibility: DatasetVisibility,

  @JsonProperty(JsonKey.Owner)
  val owner: UserID,

  @JsonProperty(JsonKey.Name)
  val name: String,

  @JsonProperty(JsonKey.ShortName)
  val shortName: String?,

  @JsonProperty(JsonKey.Summary)
  val summary: String,

  @JsonProperty(JsonKey.Description)
  val description: String?,

  @JsonProperty(JsonKey.ShortAttribution)
  val shortAttribution: String?,

  @JsonProperty(JsonKey.Origin)
  val origin: String,

  @JsonProperty(JsonKey.SourceURL)
  val sourceURL: String?,

  @JsonProperty(JsonKey.Created)
  val created: OffsetDateTime,

  @JsonProperty(JsonKey.Dependencies)
  val dependencies: Collection<DatasetDependency> = emptyList(),

  @JsonProperty(JsonKey.Publications)
  val publications: Collection<DatasetPublication> = emptyList(),

  @JsonProperty(JsonKey.Hyperlinks)
  val hyperlinks: Collection<DatasetHyperlink> = emptyList(),

  @JsonProperty(JsonKey.Organisms)
  val organisms: Collection<String> = emptyList(),

  @JsonProperty(JsonKey.Contacts)
  val contacts: Collection<DatasetContact> = emptyList(),

  @JsonProperty(JsonKey.OriginalID)
  val originalID: DatasetID? = null,

  @JsonProperty(JsonKey.RevisionHistory)
  val revisionHistory: List<DatasetRevision> = emptyList(),

  @JsonProperty(JsonKey.Properties)
  val properties: DatasetProperties? = null,
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
    const val Projects         = "projects"
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
