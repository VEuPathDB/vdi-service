package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetManifest(
  @JsonProperty(JsonKey.InputFiles)
  val inputFiles: List<DatasetFileInfo>,

  @JsonProperty(JsonKey.DataFiles)
  val dataFiles: List<DatasetFileInfo>
) {
  object JsonKey {
    const val InputFiles = "inputFiles"
    const val DataFiles  = "dataFiles"
  }
}
