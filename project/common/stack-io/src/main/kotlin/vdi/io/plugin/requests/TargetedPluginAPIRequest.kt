package vdi.io.plugin.requests

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.EventID
import vdi.model.meta.DatasetID
import vdi.model.meta.InstallTargetID

sealed class TargetedPluginAPIRequest(
  eventID: EventID,
  vdiID: DatasetID,

  @get:JsonProperty(InstallTarget)
  val installTarget: InstallTargetID,
): PluginAPIRequest(eventID, vdiID) {
  companion object JsonKey {
    const val InstallTarget = "install-target"
  }
}