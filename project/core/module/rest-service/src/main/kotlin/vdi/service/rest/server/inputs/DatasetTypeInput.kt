@file:JvmName("DatasetTypeValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.checkNonBlank
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.require
import vdi.model.data.DataType
import vdi.model.data.InstallTargetID
import vdi.model.data.DatasetType
import vdi.lib.plugin.registry.PluginRegistry
import vdi.service.rest.generated.model.DatasetTypeInput
import vdi.service.rest.generated.model.JsonField

fun DatasetTypeInput.cleanup() {
  name = name.cleanupString()
  version = version.cleanupString()
}

fun DatasetTypeInput?.validate(jPath: String, projects: Iterable<InstallTargetID>, errors: ValidationErrors) =
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

fun DatasetTypeInput.toInternal(): DatasetType =
  DatasetType(DataType.of(name), version)
