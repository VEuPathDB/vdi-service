@file:JvmName("DatasetPatchRequestValidator")
package vdi.service.rest.server.inputs

import com.networknt.schema.JsonSchema
import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import vdi.model.data.InstallTargetID
import vdi.core.install.InstallTargetRegistry
import vdi.service.rest.generated.model.*

@Suppress("DuplicatedCode") // Overlap in generated API types
internal fun DatasetPatchRequestBody.cleanup() {
  type?.value?.cleanup()
  name?.apply { cleanupString(::getValue) }
  summary?.apply { cleanupString(::getValue) }
  description?.apply { cleanupString(::getValue) }
  publications?.apply { cleanupList(::getValue, DatasetPublication?::cleanup) }
  contacts?.apply { cleanupList(::getValue, DatasetContact?::cleanup) }
  projectName?.apply { cleanupString(::getValue) }
  programName?.apply { cleanupString(::getValue) }
  relatedStudies?.apply { cleanupList(::getValue, RelatedStudy?::cleanup) }
  studyCharacteristics?.apply {
    studyDesign?.apply { cleanupString(::getValue) }
    studyType?.apply { cleanupString(::getValue) }
    countries?.apply { cleanupDistinctList(::getValue, String?::cleanup) }
    years?.clea
  }



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
