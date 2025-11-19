package vdi.model.versioning

import com.fasterxml.jackson.databind.node.ObjectNode
import kotlin.reflect.KClass
import vdi.model.meta.VersionedMetaObject

interface JSONMigrator {
  val targetVersion: MetaStructureVersion

  fun migrate(node: ObjectNode, kind: KClass<out VersionedMetaObject>): ObjectNode
}
