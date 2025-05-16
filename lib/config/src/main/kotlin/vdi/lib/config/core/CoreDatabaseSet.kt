package vdi.lib.config.core

import com.fasterxml.jackson.annotation.JsonProperty
import org.veupathdb.vdi.lib.config.DatabaseConnectionConfig

data class CoreDatabaseSet(
  @param:JsonProperty("accountDb")
  @field:JsonProperty("accountDb")
  val accountDB: DatabaseConnectionConfig?,
  @param:JsonProperty("applicationDb")
  @field:JsonProperty("applicationDb")
  val applicationDB: DatabaseConnectionConfig?,
  @param:JsonProperty("userDb")
  @field:JsonProperty("userDb")
  val userDB: DatabaseConnectionConfig?,
)
