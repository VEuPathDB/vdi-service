package vdi.core.db.app

import vdi.db.app.FallbackDatasetType
import vdi.model.data.DatasetType

class AppDBRegistryCollection(
  private val byKey: Map<DatasetType, TargetDatabaseReference>,
) : Iterable<Pair<DatasetType, TargetDatabaseReference>> {

  fun keys(): Iterable<DatasetType> = byKey.keys

  operator fun get(dataType: DatasetType) =
    byKey[dataType] ?: byKey[FallbackDatasetType]

  override fun iterator() = sequence().iterator()

  fun sequence() = sequence {
    byKey.also {
      for ((k, v) in it) {
        yield(k to v)
      }
    }
  }
}
