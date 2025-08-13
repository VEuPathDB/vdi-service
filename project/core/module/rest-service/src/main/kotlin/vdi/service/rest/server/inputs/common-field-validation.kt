package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.core.install.InstallTargetRegistry


/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Simple Type Validations                                              │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */

private val NameLengthRange = 3..1024
fun String?.validateName(jPath: String, errors: ValidationErrors) =
  reqCheckLength(jPath, NameLengthRange, errors)

private val SummaryLengthRange = 3..4000 // max size for varchar in oracle
fun String?.validateSummary(jPath: String, errors: ValidationErrors) =
  reqCheckLength(jPath, SummaryLengthRange, errors)


private val OriginLengthRange = 3..256
fun String?.validateOrigin(jPath: String, errors: ValidationErrors) =
  reqCheckLength(jPath, OriginLengthRange, errors)


/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Scalar Collection Type Validations                                   │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */

fun Collection<String?>.reqEntriesCheckLength(jPath: String, length: IntRange, errors: ValidationErrors) {
  this.forEachIndexed { i, s -> s.reqCheckLength(jPath, i, length.first, length.last, errors) }
}

fun Collection<String?>?.validateProjects(jPath: String, errors: ValidationErrors) {
  requireNonEmpty(jPath, errors) { forEachIndexed { i, p ->
    p.require(jPath, i, errors) {
      if (this !in InstallTargetRegistry)
        errors.add(jPath..i, "unknown or disabled target project")
    }
  } }
}

