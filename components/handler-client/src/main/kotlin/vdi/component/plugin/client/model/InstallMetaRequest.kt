package vdi.component.plugin.client.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta

data class InstallMetaRequest(
  @JsonProperty(vdi.component.plugin.client.FieldName.VDIID)
  val vdiID: DatasetID,

  @JsonProperty(vdi.component.plugin.client.FieldName.ProjectID)
  val projectID: ProjectID,

  @JsonProperty(vdi.component.plugin.client.FieldName.Meta)
  val meta: VDIDatasetMeta
)

