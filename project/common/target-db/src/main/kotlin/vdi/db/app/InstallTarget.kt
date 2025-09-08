package vdi.db.app

import com.networknt.schema.JsonSchema
import vdi.model.data.InstallTargetID

data class InstallTarget(
  val name: InstallTargetID,
  val installDatabase: TargetDatabaseDetails,
  val controlDatabase: TargetDatabaseDetails,
  val metaValidation: JsonSchema?,
)