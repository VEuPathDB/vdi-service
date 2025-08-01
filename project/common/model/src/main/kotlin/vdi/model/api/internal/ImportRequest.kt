package vdi.model.api.internal

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.data.DatasetID
import vdi.model.data.DatasetMetadata

/**
 * Represents the JSON body of a dataset import request made by the core VDI
 * service to a plugin server.
 */
data class ImportRequest(
  @field:JsonProperty(VDIID) val vdiID: DatasetID,
  @field:JsonProperty(JobID) val importIndex: UShort,
  @field:JsonProperty(Meta)  val meta: DatasetMetadata,
) {
  companion object JsonKey {
    const val VDIID = "vdiID"
    const val JobID = "jobID"
    const val Meta  = "meta"
  }
}
