package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.core.install.InstallTargetRegistry


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


private const val OriginMinLength = 3
private const val OriginMaxLength = 256
fun String?.validateOrigin(jPath: String, errors: ValidationErrors) =
  reqCheckLength(jPath, OriginMinLength, OriginMaxLength, errors)


/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Scalar Collection Type Validations                                   │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */

fun Collection<String?>?.validateProjects(jPath: String, errors: ValidationErrors) {
  requireNonEmpty(jPath, errors) { forEachIndexed { i, p ->
    p.require(jPath, i, errors) {
      if (this !in InstallTargetRegistry)
        errors.add(jPath..i, "unknown or disabled target project")
    }
  } }
}

