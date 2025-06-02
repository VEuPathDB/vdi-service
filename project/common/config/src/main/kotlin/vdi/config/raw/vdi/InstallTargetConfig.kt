package vdi.config.raw.vdi

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import vdi.config.raw.db.DatabaseConnectionConfig
import vdi.config.raw.vdi.characteristics.DatasetPropertySchema
import vdi.config.raw.vdi.characteristics.NullPropertySchema
import vdi.config.raw.vdi.serde.InstallTargetConfigDeserializer
import vdi.model.data.DatasetType

@JsonDeserialize(using = InstallTargetConfigDeserializer::class)
data class InstallTargetConfig(
  val enabled: Boolean = true,

  val targetName: String,

  val dataTypes: Set<DatasetType> = emptySet(),

  @param:JsonProperty("controlDb")
  @field:JsonProperty("controlDb")
  val controlDB: DatabaseConnectionConfig,

  @param:JsonProperty("dataDb")
  @field:JsonProperty("dataDb")
  val dataDB: DatabaseConnectionConfig,

  val datasetPropertySchema: DatasetPropertySchema = NullPropertySchema,
)

