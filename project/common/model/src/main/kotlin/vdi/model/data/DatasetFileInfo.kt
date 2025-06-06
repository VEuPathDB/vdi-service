package vdi.model.data

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty


data class DatasetFileInfo(
  @field:JsonProperty(JsonKey.Name)
  @field:JsonAlias("filename")
  val name: String,

  @field:JsonProperty(JsonKey.Size)
  @field:JsonAlias("fileSize")
  val size: ULong,
) {
  object JsonKey {
    const val Name = "name"
    const val Size = "size"
  }
}
