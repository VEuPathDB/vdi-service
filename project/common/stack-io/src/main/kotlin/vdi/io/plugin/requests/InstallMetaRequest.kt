package vdi.io.plugin.requests

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.EventID
import vdi.model.data.DatasetID
import vdi.model.data.DatasetMetadata
import vdi.model.data.InstallTargetID

/**
 * Represents the JSON body of a dataset meta install request made by the core
 * VDI service to a plugin server.
 */
class InstallMetaRequest(
  @JsonProperty(EventID)
  eventID: EventID,

  @JsonProperty(VDIID)
  vdiID: DatasetID,

  @JsonProperty(InstallTarget)
  installTarget: InstallTargetID,

  @get:JsonProperty(Meta)
  @param:JsonProperty(Meta)
  val meta: DatasetMetadata,
): TargetedPluginAPIRequest(eventID, vdiID, installTarget) {
  companion object JsonKey {
    const val Meta = "meta"
  }
}
