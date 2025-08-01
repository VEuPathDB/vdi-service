package vdi.model.api.internal

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.data.DatasetID
import vdi.model.data.DatasetType
import vdi.model.data.InstallTargetID

/**
 * Represents the JSON body of a dataset uninstall request made by the core VDI
 * service to a plugin server.
 */
data class UninstallRequest(
  @field:JsonProperty(VDIID)
  val vdiID: DatasetID,

  @field:JsonAlias(Legacy_ProjectID)
  @field:JsonProperty(InstallTarget)
  val installTarget: InstallTargetID,

  /**
   * Type information for the target dataset.
   *
   * The dataset type info is used to determine which database connection to use
   * for a target project.
   *
   * @since v14.0.0
   */
  @field:JsonProperty(Type)
  val type: DatasetType,
) {
  companion object JsonKey {
    const val InstallTarget = "installTarget"
    const val Type          = "type"
    const val VDIID         = "vdiID"

    const val Legacy_ProjectID = "projectID"
  }
}
