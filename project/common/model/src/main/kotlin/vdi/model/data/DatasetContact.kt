package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetContact(
  @field:JsonProperty(JsonKey.Name)
  val name: String,

  @field:JsonProperty(JsonKey.Email)
  val email: String? = null,

  @field:JsonProperty(JsonKey.Affiliation)
  val affiliation: String? = null,

  @field:JsonProperty(JsonKey.City)
  val city: String? = null,

  @field:JsonProperty(JsonKey.State)
  val state: String? = null,

  @field:JsonProperty(JsonKey.Country)
  val country: String? = null,

  @field:JsonProperty(JsonKey.Address)
  val address: String? = null,

  @field:JsonProperty(JsonKey.IsPrimary)
  val isPrimary: Boolean = false,
) {
  object JsonKey {
    const val Name        = "name"
    const val Email       = "email"
    const val Affiliation = "affiliation"
    const val City        = "city"
    const val State       = "state"
    const val Country     = "country"
    const val Address     = "address"
    const val IsPrimary   = "isPrimary"
  }
}
