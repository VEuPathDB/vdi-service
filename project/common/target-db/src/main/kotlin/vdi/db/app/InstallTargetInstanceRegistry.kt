package vdi.db.app

import vdi.model.data.DataType
import vdi.model.data.DatasetType

internal class InstallTargetInstanceRegistry(private val registry: Map<DatasetType, InstallTarget>) {
  fun asSequence() =
    registry.asSequence().map { (k, v) -> k to v }

  operator fun get(dataType: DataType): Sequence<Pair<DatasetType, InstallTarget>> =
    asSequence().filter { (t, _) -> t.name == dataType || t.isFallback }

  operator fun get(dataType: DatasetType) =
    registry[dataType] ?: registry[FallbackDatasetType]
}