package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue


/**
 * @since v1.7.0
 */
data class DatasetPublication(
  @param:JsonProperty(Identifier)
  @field:JsonProperty(Identifier)
  val identifier: String,

  @param:JsonProperty(Type)
  @field:JsonProperty(Type)
  val type: PublicationType,

  @param:JsonProperty(IsPrimary)
  @field:JsonProperty(IsPrimary)
  val isPrimary: Boolean = false,
) {
  enum class PublicationType {
    PubMed,
    DOI,
    ;

    @JsonValue
    override fun toString() =
      when (this) {
        PubMed -> "pmid"
        DOI    -> "doi"
      }

    companion object {
      @JvmStatic
      @JsonCreator
      fun fromString(raw: String): PublicationType =
        fromStringOrNull(raw)
          ?: throw IllegalArgumentException("unrecognized ${DatasetPublication::class.simpleName} value: $raw")

      @JvmStatic
      fun fromStringOrNull(raw: String): PublicationType? =
        when (raw.lowercase()) {
          "pmid" -> PubMed
          "doi"  -> DOI
          else   -> null
        }
    }
  }

  companion object JsonKey {
    const val Identifier = "identifier"
    const val IsPrimary = "isPrimary"
    const val Type = "type"
  }
}