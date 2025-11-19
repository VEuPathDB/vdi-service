package vdi.model.versioning.v1_0_0

import com.fasterxml.jackson.databind.node.ObjectNode
import kotlin.reflect.KClass
import vdi.model.versioning.JSONMigrator
import vdi.model.versioning.MetaStructureVersion
import vdi.model.meta.VersionedMetaObject

object JSONMigratorLegacy: JSONMigrator {
  override val targetVersion: MetaStructureVersion
    get() = MetaStructureVersion.v1_0_0

  override fun migrate(node: ObjectNode, kind: KClass<out VersionedMetaObject>): ObjectNode =
    throw NotImplementedError()
}