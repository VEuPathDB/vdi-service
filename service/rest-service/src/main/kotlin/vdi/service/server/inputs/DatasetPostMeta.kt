package vdi.service.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.service.generated.model.*
import vdi.service.generated.model.JsonField as JF

/**
 * Property cleanup, trim strings, remove blanks, replace empty/null lists with
 * the empty list singleton.
 *
 * For sanity's sake properties in this method should try and keep the same
 * ordering as the property definitions in the API docs.
 */
@Suppress("DuplicatedCode") // Overlap in generated API types
internal fun DatasetPostMeta.cleanup() {
  // Required Fields
  name = name.cleanupString()
  datasetType?.cleanup()
  origin = origin.cleanupString()
  dependencies = dependencies.cleanup(DatasetDependency::cleanup)
  projects
    ?.ifEmpty { null }
    ?.asSequence()
    ?.map { it?.trim() }
    ?.distinct()
    ?.toList()
    ?: emptyList()

  // Optional Fields
  shortName = shortName.cleanupString()
  shortAttribution = shortAttribution.cleanupString()
  category = category.cleanupString()
  visibility = visibility ?: DatasetVisibility.PRIVATE
  summary = summary.cleanupString()
  description = description.cleanupString()
  publications = publications.cleanup(DatasetPublication::cleanup)
  hyperlinks = hyperlinks.cleanup(DatasetHyperlink::cleanup)
  organisms = organisms
    ?.ifEmpty { null }
    ?.onEachIndexed { i, s -> organisms[i] = s.cleanupString() }
    ?: emptyList()
  contacts = contacts.cleanup(DatasetContact::cleanup)
  // createdOn skipped, it's admin use only
}

internal fun DatasetPostMeta.validate(errors: ValidationErrors) {
  // required fields
  name.validateName(JF.META..JF.NAME, errors)
  datasetType.validate(JF.META..JF.DATASET_TYPE, projects, errors)
  origin.validateOrigin(JF.META..JF.ORIGIN, errors)
  dependencies.validate(JF.META..JF.DEPENDENCIES, errors)
  projects.validateProjects(JF.META..JF.DEPENDENCIES, errors)

  // optional fields
  shortName.validateShortName(JF.META..JF.SHORT_NAME, errors)
  shortAttribution.validateShortAttribution(JF.META..JF.SHORT_ATTRIBUTION, errors)
  category.validateCategory(JF.META..JF.CATEGORY, errors)
  // visibility -enum, no validation needed)
  summary.validateSummary(JF.META..JF.SUMMARY, errors)
  // description - no rules, no validation
  publications.validate(JF.META..JF.PUBLICATIONS, errors)
  hyperlinks.validate(JF.META..JF.HYPERLINKS, errors)
  organisms.validateOrganisms(JF.META..JF.ORGANISMS, errors)
  contacts.validate(JF.META..JF.CONTACTS, errors)
  // createdOn - validation only needed for admin requests
}
