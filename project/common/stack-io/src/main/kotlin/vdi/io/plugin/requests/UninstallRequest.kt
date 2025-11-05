package vdi.io.plugin.requests

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.EventID
import vdi.model.data.DatasetID
import vdi.model.data.DatasetType
import vdi.model.data.InstallTargetID

/**
 * Represents the JSON body of a dataset uninstall request made by the core VDI
 * service to a plugin server.
 */
class UninstallRequest(
  @JsonProperty(EventID)
  eventID: EventID,

  @JsonProperty(VDIID)
  vdiID: DatasetID,

  @JsonProperty(InstallTarget)
  installTarget: InstallTargetID,

  /**
   * Type information for the target dataset.
   *
   * The dataset type info is used to determine which database connection to use
   * for a target project.
   */
  @get:JsonProperty(Type)
  @param:JsonProperty(Type)
  val type: DatasetType,
): TargettedPluginAPIRequest(eventID, vdiID, installTarget) {
  companion object JsonKey {
    const val Type = "type"
  }
}