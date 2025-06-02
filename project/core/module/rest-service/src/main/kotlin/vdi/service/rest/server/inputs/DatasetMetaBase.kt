@file:JvmName("DatasetMetaBaseValidator")
package vdi.service.rest.server.inputs

import com.networknt.schema.JsonSchema
import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import vdi.lib.install.InstallTargetRegistry
import vdi.service.rest.generated.model.*

fun DatasetMetaBase.cleanup() {
  // Required Fields
  name = name.cleanupString()
  dependencies = dependencies.cleanup(DatasetDependency::cleanup)
  origin = origin.cleanupString()
  installTargets
    ?.ifEmpty { null }
    ?.asSequence()
    ?.map { it?.trim() }
    ?.distinct()
    ?.toList()
    ?: emptyList()
  summary = summary.cleanupString()

  // Optional Fields
  contacts = contacts.cleanup(DatasetContact::cleanup)
  description = description.cleanupString()
  hyperlinks = hyperlinks.cleanup(DatasetHyperlink::cleanup)
  organisms = organisms
    ?.ifEmpty { null }
    ?.onEachIndexed { i, s -> organisms[i] = s.cleanupString() }
    ?: emptyList()
  publications = publications.cleanup(DatasetPublication::cleanup)
  shortName = shortName.cleanupString()
  shortAttribution = shortAttribution.cleanupString()
}

fun DatasetMetaBase.validate(errors: ValidationErrors) {
  // Required Fields
  name.validateName(JsonField.META..JsonField.NAME, errors)
  summary.validateSummary(JsonField.META..JsonField.SUMMARY, errors)
  origin.validateOrigin(JsonField.META..JsonField.ORIGIN, errors)
  installTargets.validateProjects(JsonField.META..JsonField.DEPENDENCIES, errors)
  dependencies.validate(JsonField.META..JsonField.DEPENDENCIES, errors)

  // Optional Fields
  contacts.validate(JsonField.META..JsonField.CONTACTS, errors)
  // description - no rules, no validation
  hyperlinks.validate(JsonField.META..JsonField.HYPERLINKS, errors)
  organisms.validateOrganisms(JsonField.META..JsonField.ORGANISMS, errors)
  publications.validate(JsonField.META..JsonField.PUBLICATIONS, errors)
  shortName.validateShortName(JsonField.META..JsonField.SHORT_NAME, errors)
  shortAttribution.validateShortAttribution(JsonField.META..JsonField.SHORT_ATTRIBUTION, errors)

  validateProperties(errors)
}

private fun DatasetMetaBase.validateProperties(errors: ValidationErrors) {
  var owner: String? = null
  var schema: JsonSchema? = null

  installTargets.forEachIndexed { i, it ->
    when {
      schema == null -> {
        owner = it
        schema = InstallTargetRegistry[it]!!.propertySchema
      }
      schema !== InstallTargetRegistry[it]!!.propertySchema -> {
        errors.add(JsonField.INSTALL_TARGETS..i, "install target $it property schema is incompatible with install target $owner")
      }
    }
  }

  properties?.also { props -> schema?.also { props.validate(it, JsonField.PROPERTIES, errors) } }
}
