package vdi.db.app

import com.networknt.schema.JsonSchema
import vdi.model.meta.InstallTargetID

data class InstallTarget(
  val id: InstallTargetID,
  val name: String,
  val installDatabase: TargetDatabaseDetails,
  val controlDatabase: TargetDatabaseDetails,
  val metaValidation: JsonSchema?,
)