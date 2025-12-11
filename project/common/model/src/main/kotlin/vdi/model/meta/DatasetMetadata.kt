package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI
import java.time.OffsetDateTime

data class DatasetMetadata(
  @param:JsonProperty(Type)
  @field:JsonProperty(Type)
  val type: DatasetType,

  /**
   * @since v1.7.0 - replaces "projects"
   */
  @param:JsonAlias(LegacyInstallTargets)
  @param:JsonProperty(InstallTargets)
  @field:JsonProperty(InstallTargets)
  val installTargets: Set<InstallTargetID>,

  @param:JsonProperty(Visibility)
  @field:JsonProperty(Visibility)
  val visibility: DatasetVisibility,

  @param:JsonProperty(Owner)
  @field:JsonProperty(Owner)
  val owner: UserID,

  /**
   * Dataset/Study name.
   */
  @param:JsonProperty(Name)
  @field:JsonProperty(Name)
  val name: String,

  @param:JsonProperty(Summary)
  @field:JsonProperty(Summary)
  val summary: String,

  @param:JsonProperty(Description)
  @field:JsonProperty(Description)
  val description: String? = null,

  @param:JsonProperty(Origin)
  @field:JsonProperty(Origin)
  val origin: String,

  @param:JsonProperty(Created)
  @field:JsonProperty(Created)
  val created: OffsetDateTime,

  @param:JsonProperty(SourceURL)
  @field:JsonProperty(SourceURL)
  val sourceURL: URI? = null,

  /**
   * Reference Genome
   */
  @param:JsonProperty(Dependencies)
  @field:JsonProperty(Dependencies)
  val dependencies: List<DatasetDependency> = emptyList(),

  /**
   * @since v1.7.0
   */
  @param:JsonProperty(Publications)
  @field:JsonProperty(Publications)
  val publications: List<DatasetPublication> = emptyList(),

  /**
   * @since v1.7.0
   */
  @param:JsonProperty(Contacts)
  @field:JsonProperty(Contacts)
  val contacts: List<DatasetContact> = emptyList(),

  /**
   * @since v1.7.0
   */
  @param:JsonProperty(ShortAttribution)
  @field:JsonProperty(ShortAttribution)
  val shortAttribution: String? = null,

  /**
   * An organizational framework that supports coordinated studies with shared
   * objectives.
   *
   * @since v1.7.0
   */
  @param:JsonProperty(ProjectName)
  @field:JsonProperty(ProjectName)
  val projectName: String? = null,

  /**
   * A global consortium of independent research centers collaborating under a
   * common theme, such as "ICEMR".
   *
   * @since v1.7.0
   */
  @param:JsonProperty(ProgramName)
  @field:JsonProperty(ProgramName)
  val programName: String? = null,

  /**
   * @since v1.7.0
   */
  @param:JsonProperty(LinkedDatasets)
  @field:JsonProperty(LinkedDatasets)
  val linkedDatasets: List<LinkedDataset> = emptyList(),

  /**
   * @since v1.7.0
   */
  @param:JsonProperty(ExperimentalOrganism)
  @field:JsonProperty(ExperimentalOrganism)
  val experimentalOrganism: DatasetOrganism? = null,

  /**
   * @since v1.7.0
   */
  @param:JsonProperty(HostOrganism)
  @field:JsonProperty(HostOrganism)
  val hostOrganism: DatasetOrganism? = null,

  /**
   * @since v1.7.0
   */
  @param:JsonProperty(Characteristics)
  @field:JsonProperty(Characteristics)
  val studyCharacteristics: DatasetCharacteristics? = null,

  /**
   * @since v1.7.0
   */
  @param:JsonProperty(ExternalIdentifiers)
  @field:JsonProperty(ExternalIdentifiers)
  val externalIdentifiers: ExternalDatasetIdentifiers? = null,

  /**
   * @since v1.7.0
   */
  @param:JsonProperty(Funding)
  @field:JsonProperty(Funding)
  val funding: List<DatasetFundingAward> = emptyList(),

  /**
   * @since v1.7.0
   */
  @param:JsonProperty(RevisionHistory)
  @field:JsonProperty(RevisionHistory)
  val revisionHistory: DatasetRevisionHistory? = null,

  @param:JsonProperty(DaysForApproval)
  @field:JsonProperty(DaysForApproval)
  val daysForApproval: Int = -1,
) {
  @get:JsonIgnore
  val shortName get() = name.take(40)

  companion object JsonKey {
    const val Contacts             = "contacts"
    const val Created              = "created"
    const val DaysForApproval      = "daysForApproval"
    const val Dependencies         = "dependencies"
    const val Description          = "description"
    const val ExperimentalOrganism = "experimentalOrganism"
    const val ExternalIdentifiers  = "externalIdentifiers"
    const val Funding              = "funding"
    const val HostOrganism         = "hostOrganism"
    const val InstallTargets       = "installTargets"
    const val Name                 = "name"
    const val Origin               = "origin"
    const val Owner                = "owner"
    const val ProgramName          = "programName"
    const val ProjectName          = "projectName"
    const val Publications         = "publications"
    const val LinkedDatasets       = "linkedDatasets"
    const val RevisionHistory      = "revisionHistory"
    const val ShortAttribution     = "shortAttribution"
    const val SourceURL            = "sourceUrl"
    const val Characteristics      = "studyCharacteristics"
    const val Summary              = "summary"
    const val Type                 = "type"
    const val Visibility           = "visibility"

    private const val LegacyInstallTargets = "projects"
  }
}
