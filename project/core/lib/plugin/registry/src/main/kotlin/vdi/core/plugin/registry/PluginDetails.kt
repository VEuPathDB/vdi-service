package vdi.core.plugin.registry

import vdi.model.meta.DatasetType

data class PluginDetails(val name: String, val dataTypes: Array<DatasetType>) {
  override fun equals(other: Any?) =
    this === other || (other is PluginDetails && other.name == name)

  override fun hashCode() =
    31 * name.hashCode() + dataTypes.contentHashCode()
}
