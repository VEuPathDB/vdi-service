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
  @JsonProperty(JSONKeys.VDIID)
  val vdiID: DatasetID,

  @JsonProperty(JSONKeys.InstallTarget)
  @JsonAlias(JSONKeys.ProjectID)
  val installTarget: InstallTargetID,

  /**
   * Type information for the target dataset.
   *
   * The dataset type info is used to determine which database connection to use
   * for a target project.
   *
   * @since v14.0.0
   */
  @JsonProperty(JSONKeys.Type)
  val type: DatasetType,
)
