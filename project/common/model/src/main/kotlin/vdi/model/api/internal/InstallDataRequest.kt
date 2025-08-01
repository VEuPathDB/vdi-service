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
  @field:JsonProperty(VDIID)
  val vdiID: DatasetID,

  @field:JsonAlias(Legacy_ProjectID)
  @field:JsonProperty(InstallTarget)
  val installTarget: InstallTargetID,
) {
  companion object JsonKey {
    const val InstallTarget = "installTarget"
    const val VDIID         = "vdiID"

    const val Legacy_ProjectID = "projectID"
  }
}