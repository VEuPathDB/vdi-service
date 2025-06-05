package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetManifest(
  @field:JsonProperty(JsonKey.InputFiles)
  val inputFiles: List<DatasetFileInfo>,

  @field:JsonProperty(JsonKey.DataFiles)
  val dataFiles: List<DatasetFileInfo>
) {
  object JsonKey {
    const val InputFiles = "inputFiles"
    const val DataFiles  = "dataFiles"
  }
}
