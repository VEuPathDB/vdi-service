package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.service.rest.generated.model.DatasetContact
import vdi.service.rest.generated.model.DatasetHyperlink
import vdi.service.rest.generated.model.DatasetPatchRequestBody
import vdi.service.rest.generated.model.DatasetPublication
import vdi.service.rest.generated.model.JsonField
import org.veupathdb.vdi.lib.common.field.ProjectID

@Suppress("DuplicatedCode") // Overlap in generated API types
internal fun vdi.service.rest.generated.model.DatasetPatchRequestBody.cleanup() {
  name = name.cleanupString()
  shortName = shortName.cleanupString()
  shortAttribution = shortAttribution.cleanupString()
  category = category.cleanupString()
  summary = summary.cleanupString()
  description = description.cleanupString()
  publications = publications.cleanup(vdi.service.rest.generated.model.DatasetPublication::cleanup)
  hyperlinks = hyperlinks.cleanup(vdi.service.rest.generated.model.DatasetHyperlink::cleanup)
  organisms = organisms
    ?.ifEmpty { null }
    ?.onEachIndexed { i, s -> organisms[i] = s.cleanupString() }
    ?: emptyList()
  contacts = contacts.cleanup(vdi.service.rest.generated.model.DatasetContact::cleanup)
  datasetType?.cleanup()
}

internal fun vdi.service.rest.generated.model.DatasetPatchRequestBody.validate(projects: Iterable<ProjectID>, errors: ValidationErrors = ValidationErrors()): ValidationErrors {
  name?.validateName(vdi.service.rest.generated.model.JsonField.NAME, errors)
  shortName.validateShortName(vdi.service.rest.generated.model.JsonField.SHORT_NAME, errors)
  shortAttribution.validateShortAttribution(vdi.service.rest.generated.model.JsonField.SHORT_ATTRIBUTION, errors)
  category.validateCategory(vdi.service.rest.generated.model.JsonField.CATEGORY, errors)
  summary.validateSummary(vdi.service.rest.generated.model.JsonField.SUMMARY, errors)
  // description (nothing to validate)
  publications.validate(vdi.service.rest.generated.model.JsonField.PUBLICATIONS, errors)
  hyperlinks.validate(vdi.service.rest.generated.model.JsonField.HYPERLINKS, errors)
  organisms.validateOrganisms(vdi.service.rest.generated.model.JsonField.ORGANISMS, errors)
  contacts.validate(vdi.service.rest.generated.model.JsonField.CONTACTS, errors)
  // pass an empty list for projects because we don't have that information yet.
  datasetType?.validate(vdi.service.rest.generated.model.JsonField.DATASET_TYPE, projects, errors)

  return errors
}
