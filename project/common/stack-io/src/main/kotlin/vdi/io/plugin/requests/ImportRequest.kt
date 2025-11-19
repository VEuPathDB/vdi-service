package vdi.io.plugin.requests

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.EventID
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata

/**
 * Represents the JSON body of a dataset import request made by the core VDI
 * service to a plugin server.
 */
class ImportRequest(
  @JsonProperty(EventID)
  eventID: EventID,

  @JsonProperty(VDIID)
  vdiID: DatasetID,

  @get:JsonProperty(JobID)
  @param:JsonProperty(JobID)
  val importIndex: UShort,

  @get:JsonProperty(Meta)
  @param:JsonProperty(Meta)
  val meta: DatasetMetadata,
): PluginAPIRequest(eventID, vdiID) {
  companion object JsonKey {
    const val JobID = "job-id"
    const val Meta  = "meta"
  }
}