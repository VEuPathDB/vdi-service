package vdi.lib.config.core

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.lib.config.common.DatabaseConnectionConfig

data class CoreDatabaseSet(
  @JsonProperty("accountDb")
  val accountDB: DatabaseConnectionConfig?,
  @JsonProperty("applicationDb")
  val applicationDB: DatabaseConnectionConfig?,
  @JsonProperty("userDb")
  val userDB: DatabaseConnectionConfig?,
)
