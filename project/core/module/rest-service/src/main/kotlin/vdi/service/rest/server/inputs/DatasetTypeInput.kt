@file:JvmName("DatasetTypeInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.reqCheckMinLength
import org.veupathdb.lib.request.validation.require
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.meta.DataType
import vdi.model.meta.DatasetType
import vdi.model.meta.InstallTargetID
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
