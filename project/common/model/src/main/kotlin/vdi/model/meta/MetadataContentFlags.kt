package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType

/**
 * Flags indicating content that a dataset maintainer has indicated must exist
 * in the dataset metadata.
 *
 * @since v1.9.0
 */
@JsonInclude(
  // Include null values during serialization.
  JsonInclude.Include.ALWAYS
)
data class MetadataContentFlags(
  /**
   * Dataset has dataset/study characteristics metadata.
   */
  @param:JsonProperty(HasCharacteristics)
  @field:JsonProperty(HasCharacteristics)
  val hasDatasetCharacteristics: FlagState = FlagState.Undefined,

  /**
   * Dataset has an external dataset sources list.
   */
  @param:JsonProperty(HasExternalSources)
  @field:JsonProperty(HasExternalSources)
  val hasDatasetSources: FlagState = FlagState.Undefined,

  /**
   * Dataset has a data usage disclaimer value.
   */
  @param:JsonProperty(HasDataDisclaimer)
  @field:JsonProperty(HasDataDisclaimer)
  val hasDataDisclaimer: FlagState = FlagState.Undefined,

  /**
   * Dataset has associated publications.
   */
  @param:JsonProperty(HasPublications)
  @field:JsonProperty(HasPublications)
  val hasPublications: FlagState = FlagState.Undefined,

  /**
   * Dataset has associated publications.
   */
  @param:JsonProperty(HasOrganismData)
  @field:JsonProperty(HasOrganismData)
  val hasOrganismData: FlagState = FlagState.Undefined,
) {
  enum class FlagState {
    /**
     * Dataset maintainer has explicitly set this flag to `true`.
     */
    True,

    /**
     * Dataset maintainer has explicitly set this flag to `false`.
     */
    False,

    /**
     * No value has been explicitly provided by the dataset maintainer(s).
     */
    Undefined,
    ;

    @get:JsonValue
    val jsonValue: Boolean?
      get() = when (this) {
        True      -> true
        False     -> false
        Undefined -> null
      }

    companion object {
      @JvmStatic
      @JsonCreator
      fun fromJson(node: JsonNode) =
        when (node.nodeType) {
          JsonNodeType.BOOLEAN -> fromBoolean(node.asBoolean())
          JsonNodeType.STRING  -> fromString(node.asText())
          JsonNodeType.NUMBER  -> fromNumber(node.numberValue())
          JsonNodeType.NULL    -> Undefined

          else -> throw IllegalArgumentException(
            "invalid ${FlagState::class.qualifiedName} value type: ${node.nodeType}"
          )
        }

      @JvmStatic
      fun fromBoolean(value: Boolean?) =
        when (value) {
          null  -> Undefined
          true  -> True
          false -> False
        }

      @JvmStatic
      fun fromNumber(value: Number?) =
        when (value) {
          null -> Undefined
          0    -> False
          1    -> True
          else -> throw IllegalArgumentException(
            "invalid ${FlagState::class.qualifiedName} number value: $value"
          )
        }

      @JvmStatic
      fun fromString(value: String?) =
        when (value?.lowercase()) {
          null  -> Undefined
          "yes" -> True
          "no"  -> False
          else  -> throw IllegalArgumentException(
            "invalid ${FlagState::class.qualifiedName} string value: $value"
          )
        }
    }
  }

  companion object JsonKey {
    const val HasCharacteristics = "hasDatasetCharacteristics"
    const val HasDataDisclaimer  = "hasDataDisclaimer"
    const val HasExternalSources = "hasDatasetSources"
    const val HasOrganismData    = "hasOrganismData"
    const val HasPublications    = "hasPublications"
  }
}
