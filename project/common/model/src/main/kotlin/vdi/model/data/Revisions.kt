package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * ID of the original dataset upload.
 */
data class DatasetRevisionHistory(
  /**
   * ID of the original dataset upload.
   */
  @field:JsonProperty(OriginalID)
  val originalID: DatasetID,

  /**
   * List of all dataset revisions, including both the original dataset upload
   * and the current revision.
   */
  @field:JsonProperty(RevisionHistory)
  val revisions: List<DatasetRevision>,
) {
  companion object JsonKey {
    const val OriginalID      = "originalId"
    const val RevisionHistory = "revisionHistory"
  }
}
