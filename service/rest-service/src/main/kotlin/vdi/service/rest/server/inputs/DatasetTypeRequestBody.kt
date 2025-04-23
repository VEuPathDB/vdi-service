package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.service.rest.generated.model.DatasetTypeRequestBody
import vdi.service.rest.generated.model.JsonField
import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import vdi.lib.plugin.registry.PluginRegistry

fun vdi.service.rest.generated.model.DatasetTypeRequestBody.cleanup() {
  name = name.cleanupString()
  version = version.cleanupString()
}

fun vdi.service.rest.generated.model.DatasetTypeRequestBody?.validate(jPath: String, projects: Iterable<ProjectID>, errors: ValidationErrors) =
  require(jPath, errors) {
    if (
      name.checkNonBlank(jPath..vdi.service.rest.generated.model.JsonField.NAME, errors)
      && version.checkNonBlank(jPath..vdi.service.rest.generated.model.JsonField.VERSION, errors)
    ) {
      val details = PluginRegistry[DataType.of(name), version]
        ?: return errors.add(jPath, "unrecognized or unavailable dataset type")

      projects.forEach {
        if (!details.appliesTo(it))
          errors.add(jPath, "dataset type is not applicable to target $it")
      }
    }
  }

fun vdi.service.rest.generated.model.DatasetTypeRequestBody.toInternal(): VDIDatasetType =
  VDIDatasetType(DataType.of(name), version)
