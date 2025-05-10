package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.lib.install.InstallTargetRegistry


/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Simple Type Validations                                              │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */

private const val NameMinLength = 3
private const val NameMaxLength = 1024
fun String?.validateName(jPath: String, errors: ValidationErrors) =
  reqCheckLength(jPath, NameMinLength, NameMaxLength, errors)


private const val SummaryMinLength = 3
private const val SummaryMaxLength = 4000 // max size for varchar in oracle
fun String?.validateSummary(jPath: String, errors: ValidationErrors) =
  reqCheckLength(jPath, SummaryMinLength, SummaryMaxLength, errors)


private const val ShortNameMinLength = 3
private const val ShortNameMaxLength = 300
fun String?.validateShortName(jPath: String, errors: ValidationErrors) =
  optCheckLength(jPath, ShortNameMinLength, ShortNameMaxLength, errors)


private const val ShortAttributionMinLength = 3
private const val ShortAttributionMaxLength = 300
fun String?.validateShortAttribution(jPath: String, errors: ValidationErrors) =
  optCheckLength(jPath, ShortAttributionMinLength, ShortAttributionMaxLength, errors)


private const val CategoryMinLength = 3
private const val CategoryMaxLength = 100
fun String?.validateCategory(jPath: String, errors: ValidationErrors) =
  optCheckLength(jPath, CategoryMinLength, CategoryMaxLength, errors)


private const val OriginMinLength = 3
private const val OriginMaxLength = 256
fun String?.validateOrigin(jPath: String, errors: ValidationErrors) =
  reqCheckLength(jPath, OriginMinLength, OriginMaxLength, errors)


/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Scalar Collection Type Validations                                   │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */


// Based on App DB vdi_control_*.dataset_organism table
private const val OrganismAbbrevMaxLength = 20
private const val OrganismAbbrevMinLength = 3

fun Iterable<String?>.validateOrganisms(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, l -> l.reqCheckLength(jPath, i, OrganismAbbrevMinLength, OrganismAbbrevMaxLength, errors) }

fun Collection<String?>?.validateProjects(jPath: String, errors: ValidationErrors) {
  requireNonEmpty(jPath, errors) { forEachIndexed { i, p ->
    p.require(jPath, i, errors) {
      if (this !in InstallTargetRegistry)
        errors.add(jPath..i, "unknown or disabled target project")
    }
  } }
}

