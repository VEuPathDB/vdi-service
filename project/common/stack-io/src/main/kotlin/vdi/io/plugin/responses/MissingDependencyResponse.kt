package vdi.io.plugin.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class MissingDependencyResponse(
  @get:JsonProperty(Warnings)
  @param:JsonProperty(Warnings)
  val warnings: Iterable<String>,
): InstallDataResponse {
  companion object JsonKey {
    const val Warnings = "warnings"
  }
}
