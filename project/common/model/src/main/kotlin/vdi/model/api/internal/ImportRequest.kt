package vdi.model.api.internal

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.data.DatasetID
import vdi.model.data.DatasetMetadata

/**
 * Represents the JSON body of a dataset import request made by the core VDI
 * service to a plugin server.
 */
data class ImportRequest(
  @JsonProperty(JSONKeys.VDIID) val vdiID: DatasetID,
  @JsonProperty(JSONKeys.JobID) val importIndex: UShort,
  @JsonProperty(JSONKeys.Meta)  val meta: DatasetMetadata,
)
