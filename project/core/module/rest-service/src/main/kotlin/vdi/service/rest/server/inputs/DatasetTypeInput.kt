@file:JvmName("DatasetTypeInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.model.data.DataType
import vdi.model.data.InstallTargetID
import vdi.model.data.DatasetType
import vdi.core.plugin.registry.PluginRegistry
import vdi.service.rest.generated.model.DatasetTypeInput
import vdi.service.rest.generated.model.JsonField

fun DatasetTypeInput?.cleanup() = this?.apply {
  cleanupString(::getName)
  cleanupString(::getVersion)
}

fun DatasetTypeInput?.validate(jPath: String, projects: Iterable<InstallTargetID>, errors: ValidationErrors) =
  require(jPath, errors) {
    if (
      name.reqCheckMinLength(jPath..JsonField.NAME, 1, errors)
      && version.reqCheckMinLength(jPath..JsonField.VERSION, 1, errors)
    ) {
      val details = PluginRegistry[toInternal()]
        ?: return errors.add(jPath, "unrecognized or unavailable dataset type")

      projects.forEach {
        if (!details.appliesTo(it))
          errors.add(jPath, "dataset type is not applicable to target $it")
      }
    }
  }

fun DatasetTypeInput.toInternal(): DatasetType =
  DatasetType(DataType.of(name), version)
