package org.veupathdb.service.vdi.server.input

import org.veupathdb.service.vdi.generated.model.DatasetTypeRequestBody
import org.veupathdb.service.vdi.generated.model.JsonField
import org.veupathdb.service.vdi.util.ValidationErrors
import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.component.plugins.PluginRegistry

internal fun DatasetTypeRequestBody.cleanup() {
  name = name?.trim()
  version = version?.trim()
}

internal fun DatasetTypeRequestBody.validate(project: ProjectID, errors: ValidationErrors, prefix: String = "") {
  val jPath = if (prefix.isNotBlank())
    prefix + "." + JsonField.DATASET_TYPE
  else
    JsonField.DATASET_TYPE

  val nameOk = name.checkNonEmpty(JsonField.NAME, jPath, errors)

  if (version.checkNonEmpty(JsonField.VERSION, jPath, errors) && nameOk) {
    val details = PluginRegistry[DataType.of(name), version]

    if (details == null)
      errors.add(jPath, "unrecognized or unavailable dataset type")
    else if (!details.appliesTo(project))
      errors.add(jPath, "dataset type is not applicable to target $project")
  }
}
