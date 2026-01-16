package vdi.config.raw.vdi

import com.fasterxml.jackson.annotation.JsonProperty

class DataTypeConfig(
  val name: String,
  val category: String,
  val version: String,

  @param:JsonProperty(KeyProjectIDs)
  @field:JsonProperty(KeyProjectIDs)
  val installTargets: Array<String>?,

  val maxFileSize: ULong = Long.MAX_VALUE.toULong(),
  val allowedFileExtensions: Array<String> = emptyArray(),
  val typeChangesEnabled: Boolean = false,
  val usesDataProperties: Boolean = false,
  val dataPropsFileNameSingular: String = "Data Properties File",
  val dataPropsFileNamePlural: String = "Data Properties Files",
) {
  private companion object {
    const val KeyProjectIDs = "projectIds"
  }
}
