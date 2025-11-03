package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class BioprojectIDReference(
  @field:JsonProperty(ID)
  val id: String,

  @field:JsonProperty(Description)
  val description: String? = null,
) {
  companion object JsonKey {
    const val ID          = "id"
    const val Description = "description"
  }
}
