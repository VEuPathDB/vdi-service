package vdi.model.data

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI
import java.time.OffsetDateTime

@JsonIgnoreProperties(
  DatasetMetadata.Legacy_Category,
  DatasetMetadata.Legacy_Organisms,
  DatasetMetadata.Legacy_ShortName,
  DatasetMetadata.Legacy_ShortAttribution,
)
data class DatasetMetadata(
  @field:JsonProperty(Type)
  val type: DatasetType,

  @field:JsonAlias(Legacy_Projects)
  @field:JsonProperty(InstallTargets)
  val installTargets: Set<InstallTargetID>,

  @field:JsonProperty(Visibility)
  val visibility: DatasetVisibility,

  @field:JsonProperty(Owner)
  val owner: UserID,

  /**
   * Dataset/Study name.
   */
  @field:JsonProperty(Name)
  val name: String,

  @field:JsonProperty(Summary)
  val summary: String,

  @field:JsonProperty(Description)
  val description: String? = null,

  @field:JsonProperty(Origin)
  val origin: String,

  @field:JsonProperty(Created)
  val created: OffsetDateTime,

  @field:JsonProperty(SourceURL)
  val sourceURL: URI? = null,

  /**
   * Reference Genome
   */
  @field:JsonProperty(Dependencies)
  val dependencies: List<DatasetDependency> = emptyList(),

  @field:JsonProperty(Publications)
  val publications: List<DatasetPublication> = emptyList(),

  @field:JsonProperty(Contacts)
  val contacts: List<DatasetContact> = emptyList(),

  /**
   * An organizational framework that supports coordinated studies with shared
   * objectives.
   */
  @field:JsonProperty(ProjectName)
  val projectName: String? = null,

  /**
   * A global consortium of independent research centers collaborating under a
   * common theme, such as "ICEMR".
   */
  @field:JsonProperty(ProgramName)
  val programName: String? = null,

  @field:JsonProperty(RelatedStudies)
  val relatedStudies: List<RelatedStudy> = emptyList(),

  @field:JsonProperty(ExperimentalOrganism)
  val experimentalOrganism: DatasetOrganism? = null,

  @field:JsonProperty(HostOrganism)
  val hostOrganism: DatasetOrganism? = null,

  @field:JsonProperty(StudyCharacteristics)
  val studyCharacteristics: StudyCharacteristics? = null,

  @field:JsonProperty(ExternalIdentifiers)
  val externalIdentifiers: ExternalDatasetIdentifiers? = null,

  @field:JsonProperty(Funding)
  val funding: List<DatasetFundingAward> = emptyList(),

  @field:JsonProperty(RevisionHistory)
  val revisionHistory: DatasetRevisionHistory? = null,
) {
  companion object JsonKey {
    const val Contacts             = "contacts"
    const val Created              = "created"
    const val Dependencies         = "dependencies"
    const val Description          = "description"
    const val ExperimentalOrganism = "experimentalOrganism"
    const val ExternalIdentifiers  = "externalIdentifiers"
    const val Funding              = "funding"
    const val HostOrganism         = "hostOrganism"
    const val InstallTargets       = "installTargets"
    const val Name                 = "name"
    const val Organisms            = "organisms"
    const val Origin               = "origin"
    const val Owner                = "owner"
    const val ProgramName          = "programName"
    const val ProjectName          = "projectName"
    const val Properties           = "properties"
    const val Publications         = "publications"
    const val RelatedStudies       = "relatedStudies"
    const val RevisionHistory      = "revisionHistory"
    const val ShortAttribution     = "shortAttribution"
    const val ShortName            = "shortName"
    const val SourceURL            = "sourceUrl"
    const val StudyCharacteristics = "studyCharacteristics"
    const val Summary              = "summary"
    const val Type                 = "type"
    const val Visibility           = "visibility"

    // Legacy fields: no longer used, but may still exist in metadata for older
    // datasets.
    const val Legacy_Category         = "category"
    const val Legacy_Organisms        = "organisms"
    const val Legacy_Projects         = "projects"
    const val Legacy_ShortAttribution = "shortAttribution"
    const val Legacy_ShortName        = "shortName"
  }
}
