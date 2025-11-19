package vdi.io.plugin.requests

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.EventID
import vdi.model.meta.DatasetID

sealed class PluginAPIRequest(
  @get:JsonProperty(EventID)
  val eventID: EventID,

  @get:JsonProperty(VDIID)
  val vdiID: DatasetID,
) {
  companion object JsonKey {
    const val EventID = "event-id"
    const val VDIID = "vdi-id"
  }
}

