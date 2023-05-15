package org.veupathdb.vdi.lib.handler.client.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.handler.client.FieldName

data class ImportRequestDetails(
  @JsonProperty(FieldName.VDIID)
  val vdiID: DatasetID,

  @JsonProperty(FieldName.Meta)
  val meta: VDIDatasetMeta
)