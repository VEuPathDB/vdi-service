package vdi.model.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.time.OffsetDateTime

open class DatasetRevision(
  @field:JsonProperty(JsonKey.Action)
  val action: Action,

  @field:JsonProperty(Timestamp)
  val timestamp: OffsetDateTime,

  @field:JsonProperty(RevisionID)
  val revisionID: DatasetID,

  @field:JsonProperty(RevisionNote)
  val revisionNote: String
) {
  companion object JsonKey {
    const val Action       = "action"
    const val RevisionID   = "revisionId"
    const val RevisionNote = "revisionNote"
    const val Timestamp    = "timestamp"
  }

  override fun toString() =
    "DatasetRevision(action=$action, timestamp=$timestamp, revisionID=$revisionID, revisionNote=$revisionNote)"

  override fun equals(other: Any?): Boolean {
    return other is DatasetRevision && (
      other === this || (
        other.action == this.action
        && other.timestamp == this.timestamp
        && other.revisionID == this.revisionID
        && other.revisionNote == this.revisionNote
      ))
  }

  override fun hashCode(): Int {
    var result = action.hashCode()
    result = 31 * result + timestamp.hashCode()
    result = 31 * result + revisionID.hashCode()
    result = 31 * result + revisionNote.hashCode()
    return result
  }

  /**
   * Represents the type of action taken to update a VDI dataset's data.
   *
   * @since 18.0.0
   */
  enum class Action(
    /**
     * Numeric ID of the action type.
     */
    val id: UByte,

    /**
     * String representation of the action type.
     */
    @get:JsonValue
    val stringValue: String
  ) {
    /**
     * The dataset was replaced with an updated/corrected version.
     */
    Revise(0u, "revise"),

    /**
     * A new dataset was added in addition to the original as an extension of the
     * data in the original dataset.
     */
    Extend(1u, "extend"),
    ;

    override fun toString() = stringValue

    companion object {
      @JsonCreator
      fun fromString(value: String) =
        fromStringOrNull(value)
          ?: throw IllegalArgumentException("unrecognized DatasetRevision.Action value: $value")

      fun fromStringOrNull(value: String) =
        entries.firstOrNull { it.stringValue == value }

      fun fromID(id: UByte) =
        fromIDOrNull(id)
          ?: throw IllegalArgumentException("unrecognized DatasetRevision.Action id: $id")

      fun fromIDOrNull(id: UByte): Action? {
        for (e in entries)
          if (e.id == id)
            return e

        return null
      }
    }
  }
}
