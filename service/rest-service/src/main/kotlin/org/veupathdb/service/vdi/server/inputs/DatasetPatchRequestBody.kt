package org.veupathdb.service.vdi.server.inputs

import org.veupathdb.lib.request.validation.*
import org.veupathdb.service.vdi.generated.model.DatasetContact
import org.veupathdb.service.vdi.generated.model.DatasetHyperlink
import org.veupathdb.service.vdi.generated.model.DatasetPatchRequestBody
import org.veupathdb.service.vdi.generated.model.DatasetPublication
import org.veupathdb.service.vdi.generated.model.JsonField
import org.veupathdb.vdi.lib.common.field.ProjectID

@Suppress("DuplicatedCode") // Overlap in generated API types
internal fun DatasetPatchRequestBody.cleanup() {
  name = name.cleanupString()
  shortName = shortName.cleanupString()
  shortAttribution = shortAttribution.cleanupString()
  category = category.cleanupString()
  summary = summary.cleanupString()
  description = description.cleanupString()
  publications = publications.cleanup(DatasetPublication::cleanup)
  hyperlinks = hyperlinks.cleanup(DatasetHyperlink::cleanup)
  organisms = organisms
    ?.ifEmpty { null }
    ?.onEachIndexed { i, s -> organisms[i] = s.cleanupString() }
    ?: emptyList()
  contacts = contacts.cleanup(DatasetContact::cleanup)
  datasetType?.cleanup()
}

internal fun DatasetPatchRequestBody.validate(projects: List<ProjectID>, errors: ValidationErrors = ValidationErrors()): ValidationErrors {
  name?.validateName(JsonField.NAME, errors)
  shortName.validateShortName(JsonField.SHORT_NAME, errors)
  shortAttribution.validateShortAttribution(JsonField.SHORT_ATTRIBUTION, errors)
  category.validateCategory(JsonField.CATEGORY, errors)
  summary.validateSummary(JsonField.SUMMARY, errors)
  // description (nothing to validate)
  publications.validate(JsonField.PUBLICATIONS, errors)
  hyperlinks.validate(JsonField.HYPERLINKS, errors)
  organisms.validateOrganisms(JsonField.ORGANISMS, errors)
  contacts.validate(JsonField.CONTACTS, errors)
  // pass an empty list for projects because we don't have that information yet.
  datasetType?.validate(JsonField.DATASET_TYPE, projects, errors)

  return errors
}
