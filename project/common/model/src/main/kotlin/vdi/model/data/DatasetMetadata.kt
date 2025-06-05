package vdi.model.data

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

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

  @field:JsonProperty(JsonKey.ShortName)
  val shortName: String?,

  @field:JsonProperty(JsonKey.Summary)
  val summary: String,

  @field:JsonProperty(JsonKey.Description)
  val description: String?,

  @field:JsonProperty(JsonKey.ShortAttribution)
  val shortAttribution: String?,

  @field:JsonProperty(JsonKey.Origin)
  val origin: String,

  @field:JsonProperty(JsonKey.SourceURL)
  val sourceURL: String?,

  @field:JsonProperty(JsonKey.Created)
  val created: OffsetDateTime,

  @field:JsonProperty(JsonKey.Dependencies)
  val dependencies: Collection<DatasetDependency> = emptyList(),

  @field:JsonProperty(JsonKey.Publications)
  val publications: Collection<DatasetPublication> = emptyList(),

  @field:JsonProperty(JsonKey.Hyperlinks)
  val hyperlinks: Collection<DatasetHyperlink> = emptyList(),

  @field:JsonProperty(JsonKey.Organisms)
  val organisms: Collection<String> = emptyList(),

  @field:JsonProperty(JsonKey.Contacts)
  val contacts: Collection<DatasetContact> = emptyList(),

  @field:JsonProperty(JsonKey.OriginalID)
  val originalID: DatasetID? = null,

  @field:JsonProperty(JsonKey.RevisionHistory)
  val revisionHistory: List<DatasetRevision> = emptyList(),

  @field:JsonProperty(JsonKey.Properties)
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
