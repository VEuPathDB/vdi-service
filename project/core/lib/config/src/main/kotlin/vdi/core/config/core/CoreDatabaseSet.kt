package vdi.core.config.core

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.config.raw.db.DatabaseConnectionConfig

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
