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
   * List of target sites/projects that the dataset should be installed into.
   *
   * @since v1.7.0 - replaces "projects"
   */
  @param:JsonAlias(LegacyInstallTargets)
  @param:JsonProperty(InstallTargets)
  @field:JsonProperty(InstallTargets)
  val installTargets: Set<InstallTargetID>,

  /**
   * Visibility of the dataset in the install-target sites.
   */
  @param:JsonProperty(Visibility)
  @field:JsonProperty(Visibility)
  val visibility: DatasetVisibility,

  /**
   * VPDB user ID of the user who created the dataset.
   */
  @param:JsonProperty(Owner)
  @field:JsonProperty(Owner)
  val owner: UserID,

  /**
   * Dataset/Study name.
   */
  @param:JsonProperty(Name)
  @field:JsonProperty(Name)
  val name: String,

  /**
   * Short summary of the dataset.
   *
   * Defaults to the dataset name value for compatibility with older datasets
   * that were created before the summary field was required by the REST API.
   */
  @param:JsonProperty(Summary)
  @field:JsonProperty(Summary)
  val summary: String = "",

  @param:JsonProperty(Description)
  @field:JsonProperty(Description)
  val description: String? = null,

  /**
   * Label/identifier for the source of the dataset upload.
   *
   * For uploads through VPDB, this would be "direct".  If uploaded through a
   * third party site, it would be a name for that site.
   */
  @param:JsonProperty(Origin)
  @field:JsonProperty(Origin)
  val origin: String,

  @param:JsonProperty(Created)
  @field:JsonProperty(Created)
  val created: OffsetDateTime,

  /**
   * URL the data was retrieved from if the dataset was submitted using a data
   * url instead of a file upload.
   */
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

  /**
   * Days until automatic access request approval.
   *
   * A value of `-1` disables automated approvals.
   *
   * @since v1.7.0
   */
  @param:JsonProperty(DaysForApproval)
  @field:JsonProperty(DaysForApproval)
  val daysForApproval: Int = -1,

  /**
   * Optional disclaimer about the dataset data.
   *
   * @since v1.7.0
   */
  @param:JsonProperty(DataDisclaimer)
  @field:JsonProperty(DataDisclaimer)
  val dataDisclaimer: String? = null,
) {
  /**
   * Truncated name for the dataset.
   *
   * @since v1.7.0
   */
  @get:JsonIgnore
  val shortName get() = name.take(40)

  companion object JsonKey {
    const val Contacts            = "contacts"
    const val Created             = "created"
    const val DataDisclaimer      = "dataDisclaimer"
    const val DaysForApproval     = "daysForApproval"
    const val Dependencies        = "dependencies"
    const val Description         = "description"
    const val ExternalIdentifiers = "externalIdentifiers"
    const val Funding             = "funding"
    const val InstallTargets      = "installTargets"
    const val Name                = "name"
    const val Origin              = "origin"
    const val Owner               = "owner"
    const val ProgramName         = "programName"
    const val ProjectName         = "projectName"
    const val Publications        = "publications"
    const val LinkedDatasets      = "linkedDatasets"
    const val RevisionHistory     = "revisionHistory"
    const val ShortAttribution    = "shortAttribution"
    const val SourceURL           = "sourceUrl"
    const val Characteristics     = "studyCharacteristics"
    const val Summary             = "summary"
    const val Type                = "type"
    const val Visibility          = "visibility"

    private const val LegacyInstallTargets = "projects"
  }
}
