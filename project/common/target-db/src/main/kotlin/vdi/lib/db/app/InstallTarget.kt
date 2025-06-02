package vdi.lib.db.app

import com.fasterxml.jackson.databind.node.ObjectNode
import vdi.model.data.InstallTargetID

data class InstallTarget(
  val name: InstallTargetID,
  val installDatabase: TargetDatabaseDetails,
  val controlDatabase: TargetDatabaseDetails,
  val propertySchema: ObjectNode,
)