package org.veupathdb.vdi.lib.handler.client.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.handler.client.FieldName

data class InstallMetaRequest(
  @JsonProperty(FieldName.VDIID)
  val vdiID: DatasetID,

  @JsonProperty(FieldName.ProjectID)
  val projectID: ProjectID,

  @JsonProperty(FieldName.Meta)
  val meta: VDIDatasetMeta
)

