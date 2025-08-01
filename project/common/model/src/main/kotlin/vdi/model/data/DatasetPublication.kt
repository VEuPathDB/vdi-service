package vdi.model.data

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue


data class DatasetPublication(
  @field:JsonAlias(Legacy_PubMedID)
  @field:JsonProperty(Identifier)
  val identifier: String,

  // default provided for backwards compatibility with values that predate the
  // possibility of non-pubmed publications.
  @field:JsonProperty(Type)
  val type: PublicationType = PublicationType.PubMed,

  @field:JsonProperty(Citation)
  val citation: String? = null,

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

      fun fromStringOrNull(raw: String): PublicationType? =
        when (raw.lowercase()) {
          "pmid" -> PubMed
          "doi"  -> DOI
          else   -> null
        }
    }
  }

  companion object JsonKey {
    const val Citation = "citation"
    const val Identifier = "identifier"
    const val IsPrimary = "isPrimary"
    const val Type = "type"

    const val Legacy_PubMedID = "pubmedId"
  }
}