package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * ID of the original dataset upload.
 *
 * @since v1.7.0
 */
data class DatasetRevisionHistory(
  /**
   * ID of the original dataset upload.
   */
  @param:JsonProperty(OriginalID)
  @field:JsonProperty(OriginalID)
  val originalID: DatasetID,

  /**
   * List of all dataset revisions, including both the original dataset upload
   * and the current revision.
   */
  @param:JsonProperty(Revisions)
  @field:JsonProperty(Revisions)
  val revisions: List<DatasetRevision>,
) {
  companion object JsonKey {
    const val OriginalID = "originalId"
    const val Revisions  = "revisions"
  }
}
