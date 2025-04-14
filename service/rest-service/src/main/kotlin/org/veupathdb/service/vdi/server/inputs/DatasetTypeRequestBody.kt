package org.veupathdb.service.vdi.server.inputs

import org.veupathdb.lib.request.validation.*
import org.veupathdb.service.vdi.generated.model.DatasetTypeRequestBody
import org.veupathdb.service.vdi.generated.model.JsonField
import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import vdi.component.plugins.PluginRegistry

fun DatasetTypeRequestBody.cleanup() {
  name = name.cleanupString()
  version = version.cleanupString()
}

fun DatasetTypeRequestBody?.validate(jPath: String, projects: List<ProjectID>, errors: ValidationErrors) =
  require(jPath, errors) {
    if (
      name.checkNonBlank(jPath..JsonField.NAME, errors)
      && version.checkNonBlank(jPath..JsonField.VERSION, errors)
    ) {
      val details = PluginRegistry[DataType.of(name), version]
        ?: return errors.add(jPath, "unrecognized or unavailable dataset type")

      projects.forEach {
        if (!details.appliesTo(it))
          errors.add(jPath, "dataset type is not applicable to target $it")
      }
    }
  }

fun DatasetTypeRequestBody.toInternal(): VDIDatasetType =
  VDIDatasetType(DataType.of(name), version)
