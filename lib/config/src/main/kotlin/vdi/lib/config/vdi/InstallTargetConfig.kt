package vdi.lib.config.vdi

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.veupathdb.vdi.lib.config.DatabaseConnectionConfig

@JsonDeserialize(using = InstallTargetConfigDeserializer::class)
data class InstallTargetConfig(
  val enabled: Boolean = true,

  val targetName: String,

  val dataTypes: Set<String> = emptySet(),

  @param:JsonProperty("controlDb")
  @field:JsonProperty("controlDb")
  val controlDB: DatabaseConnectionConfig,

  @param:JsonProperty("dataDb")
  @field:JsonProperty("dataDb")
  val dataDB: DatabaseConnectionConfig,

  val datasetPropertySchema: DatasetPropertySchema = NullPropertySchema,
)

