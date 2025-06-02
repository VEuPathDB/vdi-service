@file:JvmName("DatasetPatchRequestValidator")
package vdi.service.rest.server.inputs

import com.networknt.schema.JsonSchema
import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import vdi.model.data.InstallTargetID
import vdi.lib.install.InstallTargetRegistry
import vdi.service.rest.generated.model.*

@Suppress("DuplicatedCode") // Overlap in generated API types
internal fun DatasetPatchRequestBody.cleanup() {
  name = name.cleanupString()
  shortName = shortName.cleanupString()
  shortAttribution = shortAttribution.cleanupString()
  summary = summary.cleanupString()
  description = description.cleanupString()
  publications = publications?.cleanup(DatasetPublication::cleanup)
  hyperlinks = hyperlinks?.cleanup(DatasetHyperlink::cleanup)
  organisms = organisms
    ?.ifEmpty { null }
    ?.onEachIndexed { i, s -> organisms[i] = s.cleanupString() }
  contacts = contacts?.cleanup(DatasetContact::cleanup)
  datasetType?.cleanup()
}

@Suppress("DuplicatedCode")
internal fun DatasetPatchRequestBody.validate(projects: Iterable<InstallTargetID>, errors: ValidationErrors = ValidationErrors()): ValidationErrors {
  name?.validateName(JsonField.NAME, errors)
  shortName.validateShortName(JsonField.SHORT_NAME, errors)
  shortAttribution.validateShortAttribution(JsonField.SHORT_ATTRIBUTION, errors)
  summary?.validateSummary(JsonField.SUMMARY, errors)
  // description (nothing to validate)
  publications?.validate(JsonField.PUBLICATIONS, errors)
  hyperlinks?.validate(JsonField.HYPERLINKS, errors)
  organisms?.validateOrganisms(JsonField.ORGANISMS, errors)
  contacts?.validate(JsonField.CONTACTS, errors)
  // pass an empty list for projects because we don't have that information yet.
  datasetType?.validate(JsonField.DATASET_TYPE, projects, errors)

  properties?.takeUnless { it.isEmpty }?.also { props ->
    var owner: String? = null
    var schema: JsonSchema? = null

    projects.forEachIndexed { i, it ->
      when {
        schema == null                                        -> {
          owner = it
          schema = InstallTargetRegistry[it]!!.propertySchema
        }
        schema !== InstallTargetRegistry[it]!!.propertySchema -> {
          errors.add(JsonField.INSTALL_TARGETS..i, "install target $it property schema is incompatible with install target $owner")
        }
      }
    }

    schema?.also { props.validate(it, JsonField.PROPERTIES, errors) }
  }

  return errors
}
