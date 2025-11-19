package vdi.model.versioning.v1_7_0

import com.fasterxml.jackson.databind.node.ObjectNode
import kotlin.reflect.KClass
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.DatasetFileInfo
import vdi.model.meta.DatasetManifest
import vdi.model.versioning.JSONMigrator
import vdi.model.versioning.MetaStructureVersion
import vdi.model.meta.VersionedMetaObject
import vdi.model.versioning.move

private typealias Migrator = (ObjectNode) -> ObjectNode

internal object JSONMigrator1_7_0: JSONMigrator {
  private val migrators: Map<KClass<out VersionedMetaObject>, Migrator> = mapOf(
    DatasetFileInfo::class to ::migrateFileInfo,
    DatasetManifest::class to ::migrateManifest,
    DatasetMetadata::class to ::migrateMetadata,
  )

  override val targetVersion: MetaStructureVersion
    get() = MetaStructureVersion.v1_7_0

  override fun migrate(node: ObjectNode, kind: KClass<out VersionedMetaObject>) =
    migrators[kind]?.invoke(node) ?: node

  private inline val LegacyInputsKey get() = "inputFiles"
  private inline val LegacyDataKey get() = "dataFiles"
  internal fun migrateManifest(node: ObjectNode) =
    node.apply {
      move(LegacyInputsKey, DatasetManifest.RawUploadFiles)
      move(LegacyDataKey, DatasetManifest.InstallableFiles)
    }


  private inline val LegacyFileNameKey get() = "filename"
  private inline val LegacyFileSizeKey get() = "fileSize"
  /**
   * Migrate the [DatasetFileInfo] object structure from legacy to v1.7.0.
   */
  internal fun migrateFileInfo(node: ObjectNode) =
    node.apply {
      move(LegacyFileNameKey, DatasetFileInfo.Name)
      move(LegacyFileSizeKey, DatasetFileInfo.Size)
    }


  private inline val LegacyInstallTargetsKey get() = "projects"
  internal fun migrateMetadata(node: ObjectNode) =
    node.apply {
      move(LegacyInstallTargetsKey, DatasetMetadata.InstallTargets)
    }
}