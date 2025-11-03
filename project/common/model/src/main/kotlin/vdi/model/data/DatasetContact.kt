package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import vdi.model.compat.Upgrade_Contact_StringToFields

@JsonDeserialize(using = Upgrade_Contact_StringToFields::class)
data class DatasetContact(
  @field:JsonProperty(FirstName)
  val firstName: String,

  @field:JsonProperty(MiddleName)
  val middleName: String? = null,

  @field:JsonProperty(LastName)
  val lastName: String,

  @field:JsonProperty(Email)
  val email: String? = null,

  @field:JsonProperty(Affiliation)
  val affiliation: String? = null,

  @field:JsonProperty(Country)
  val country: String? = null,

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

    const val Legacy_Name = "name"
  }
}
