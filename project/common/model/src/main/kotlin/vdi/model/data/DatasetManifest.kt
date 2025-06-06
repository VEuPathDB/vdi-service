package vdi.model.data

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetManifest(
  @field:JsonProperty(JsonKey.UserUpload)
  @field:JsonAlias("inputFiles")
  val userUploadFiles: List<DatasetFileInfo>,

  @field:JsonProperty(JsonKey.InstallReady)
  @field:JsonAlias("dataFiles")
  val installReadyFiles: List<DatasetFileInfo> = emptyList()
) {
  object JsonKey {
    const val UserUpload   = "userUpload"
    const val InstallReady = "installReady"
  }
}
