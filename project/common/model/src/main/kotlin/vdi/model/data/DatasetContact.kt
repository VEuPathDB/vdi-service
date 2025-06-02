package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetContact(
  @JsonProperty(JsonKey.Name)
  val name: String,

  @JsonProperty(JsonKey.Email)
  val email: String?,

  @JsonProperty(JsonKey.Affiliation)
  val affiliation: String?,

  @JsonProperty(JsonKey.City)
  val city: String?,

  @JsonProperty(JsonKey.State)
  val state: String?,

  @JsonProperty(JsonKey.Country)
  val country: String?,

  @JsonProperty(JsonKey.Address)
  val address: String?,

  @JsonProperty(JsonKey.IsPrimary)
  val isPrimary: Boolean,
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
