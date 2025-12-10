package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetFileInfo(
  @param:JsonAlias(LegacyName)
  @field:JsonAlias(LegacyName)
  @param:JsonProperty(Name)
  @field:JsonProperty(Name)
  val name: String,

  @param:JsonAlias(LegacySize)
  @field:JsonAlias(LegacySize)
  @param:JsonProperty(Size)
  @field:JsonProperty(Size)
  val size: ULong,
) {
  companion object JsonKey {
    const val Name = "name"
    const val Size = "size"

    private const val LegacyName = "filename"
    private const val LegacySize = "fileSize"
  }
}