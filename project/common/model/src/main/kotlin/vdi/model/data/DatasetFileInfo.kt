package vdi.model.data

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty


data class DatasetFileInfo(
  @field:JsonProperty(Name)
  @field:JsonAlias(Legacy_FileName)
  val name: String,

  @field:JsonProperty(Size)
  @field:JsonAlias(Legacy_FileSize)
  val size: ULong,
) {
  companion object JsonKey {
    const val Name = "name"
    const val Size = "size"

    const val Legacy_FileName = "filename"
    const val Legacy_FileSize = "fileSize"
  }
}
