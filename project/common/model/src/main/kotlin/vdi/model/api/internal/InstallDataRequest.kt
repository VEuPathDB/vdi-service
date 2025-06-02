package vdi.model.api.internal

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID

/**
 * Represents the JSON body of a dataset data install request made by the core
 * VDI service to a plugin server.
 */
data class InstallDataRequest(
  @JsonProperty(JSONKeys.VDIID)
  val vdiID: DatasetID,

  @JsonAlias(JSONKeys.ProjectID)
  @JsonProperty(JSONKeys.InstallTarget)
  val installTarget: InstallTargetID,
)