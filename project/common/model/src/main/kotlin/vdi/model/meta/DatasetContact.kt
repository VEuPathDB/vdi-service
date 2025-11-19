package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @since v1.7.0
 */
data class DatasetContact(
  @param:JsonProperty(FirstName)
  @field:JsonProperty(FirstName)
  val firstName: String,

  @param:JsonProperty(MiddleName)
  @field:JsonProperty(MiddleName)
  val middleName: String? = null,

  @param:JsonProperty(LastName)
  @field:JsonProperty(LastName)
  val lastName: String,

  @param:JsonProperty(Email)
  @field:JsonProperty(Email)
  val email: String? = null,

  @param:JsonProperty(Affiliation)
  @field:JsonProperty(Affiliation)
  val affiliation: String? = null,

  @param:JsonProperty(Country)
  @field:JsonProperty(Country)
  val country: String? = null,

  @param:JsonProperty(IsPrimary)
  @field:JsonProperty(IsPrimary)
  val isPrimary: Boolean = false,
) {
  companion object JsonKey {
    const val FirstName   = "firstName"
    const val MiddleName  = "middleName"
    const val LastName    = "lastName"
    const val Email       = "email"
    const val Affiliation = "affiliation"
    const val Country     = "country"
    const val IsPrimary   = "isPrimary"
  }
}
