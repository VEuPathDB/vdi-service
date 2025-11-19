package vdi.config.raw.vdi

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.networknt.schema.JsonSchema
import vdi.config.raw.db.DatabaseConnectionConfig
import vdi.config.raw.vdi.serde.InstallTargetConfigDeserializer
import vdi.model.meta.DatasetType

@JsonDeserialize(using = InstallTargetConfigDeserializer::class)
data class InstallTargetConfig(
  @param:JsonProperty(JsonKey.Enabled)
  @field:JsonProperty(JsonKey.Enabled)
  val enabled: Boolean = true,

  @param:JsonProperty(JsonKey.TargetName)
  @field:JsonProperty(JsonKey.TargetName)
  val targetName: String,

  @param:JsonProperty(JsonKey.DataTypes)
  @field:JsonProperty(JsonKey.DataTypes)
  val dataTypes: Set<DatasetType> = emptySet(),

  @param:JsonProperty(JsonKey.ControlDB)
  @field:JsonProperty(JsonKey.ControlDB)
  val controlDB: DatabaseConnectionConfig,

  @param:JsonProperty(JsonKey.DataDB)
  @field:JsonProperty(JsonKey.DataDB)
  val dataDB: DatabaseConnectionConfig,

  @param:JsonProperty(JsonKey.MetaValidation)
  @field:JsonProperty(JsonKey.MetaValidation)
  val metaValidation: JsonSchema? = null,

  @param:JsonProperty(JsonKey.FileRoot)
  @field:JsonProperty(JsonKey.FileRoot)
  val datasetFileRoot: String? = null,
) {
  internal object JsonKey {
    const val ControlDB      = "controlDb"
    const val DataDB         = "dataDb"
    const val DataTypes      = "dataTypes"
    const val Enabled        = "enabled"
    const val FileRoot       = "datasetFileRoot"
    const val MetaValidation = "metaValidation"
    const val TargetName     = "targetName"
  }
}

