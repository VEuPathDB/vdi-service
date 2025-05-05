package vdi.lib.config.vdi

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.lib.config.common.DatabaseConnectionConfig

data class InstallTargetConfig(
  val enabled: Boolean,

  val targetName: String,

  val dataTypes: Set<String> = emptySet(),

  @JsonProperty("controlDb")
  val controlDB: DatabaseConnectionConfig,

  @JsonProperty("dataDb")
  val dataDB: DatabaseConnectionConfig,

  val datasetPropertySchema: DatasetPropertySchema = NullPropertySchema,
)

