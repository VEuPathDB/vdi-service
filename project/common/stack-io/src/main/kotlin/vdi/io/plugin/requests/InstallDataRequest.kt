package vdi.io.plugin.requests

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.EventID
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID

/**
 * Represents the JSON body of a dataset data install request made by the core
 * VDI service to a plugin server.
 */
class InstallDataRequest(
  @JsonProperty(EventID)
  eventID: EventID,

  @JsonProperty(VDIID)
  vdiID: DatasetID,

  @JsonProperty(InstallTarget)
  installTarget: InstallTargetID,
): TargetedPluginAPIRequest(eventID, vdiID, installTarget)