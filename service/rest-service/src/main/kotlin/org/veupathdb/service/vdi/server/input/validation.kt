@file:Suppress("NOTHING_TO_INLINE")
package org.veupathdb.service.vdi.server.input

import org.veupathdb.service.vdi.config.Options
import org.veupathdb.service.vdi.generated.model.DatasetContact
import org.veupathdb.service.vdi.generated.model.DatasetHyperlink
import org.veupathdb.service.vdi.generated.model.DatasetPublication
import org.veupathdb.service.vdi.generated.model.JsonField
import org.veupathdb.service.vdi.server.input.validate
import org.veupathdb.service.vdi.util.ValidationErrors
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Simple Type Validations                                              │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */

private const val NameMinLength = 3
private const val NameMaxLength = 1024
internal fun String?.validateName(errors: ValidationErrors, prefix: String = "") {
  if (checkNonEmpty(JsonField.NAME, prefix, errors))
    checkLength(JsonField.NAME, prefix, NameMinLength, NameMaxLength, errors)
}

private const val SummaryMinLength = 3
private const val SummaryMaxLength = 4000 // max size for varchar in oracle
internal fun String.validateSummary(errors: ValidationErrors, prefix: String = "") =
  checkLength(JsonField.SUMMARY, prefix, SummaryMinLength, SummaryMaxLength, errors)


private const val ShortNameMinLength = 3
private const val ShortNameMaxLength = 300
internal fun String.validateShortName(errors: ValidationErrors, prefix: String = "") =
  checkLength(JsonField.SHORT_NAME, prefix, ShortNameMinLength, ShortNameMaxLength, errors)


private const val ShortAttributionMinLength = 3
private const val ShortAttributionMaxLength = 300
internal fun String.validateShortAttribution(errors: ValidationErrors, prefix: String = "") =
  checkLength(JsonField.SHORT_ATTRIBUTION, prefix, ShortAttributionMinLength, ShortAttributionMaxLength, errors)


private const val CategoryMinLength = 3
private const val CategoryMaxLength = 100
internal fun String.validateCategory(errors: ValidationErrors, prefix: String = "") =
  checkLength(JsonField.CATEGORY, prefix, CategoryMinLength, CategoryMaxLength, errors)


private const val OriginMinLength = 3
private const val OriginMaxLength = 256
internal fun String?.validateOrigin(errors: ValidationErrors, prefix: String = "") {
  if (checkNonEmpty(JsonField.ORIGIN, prefix, errors))
    checkLength(JsonField.ORIGIN, prefix, OriginMinLength, OriginMaxLength, errors)
}


/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Collection Type Validations                                          │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */

internal fun Collection<DatasetPublication>.validate(errors: ValidationErrors, prefix: String = "") {
  if (isEmpty())
    return

  var hasPrimary = false

  forEachIndexed { i, pub ->
    pub.validate(prefix, i, errors)

    if (hasPrimary)
      errors.add("${JsonField.PUBLICATIONS}[$i]".withJPathPrefix(prefix), "only one publication may be marked as primary")
    else
      hasPrimary = true
  }

  if (!hasPrimary)
    errors.add(JsonField.PUBLICATIONS.withJPathPrefix(prefix), "one publication must be marked as primary")
}

internal fun Collection<DatasetHyperlink?>.validate(errors: ValidationErrors, prefix: String = "") =
  forEachIndexed { i, link -> link.validate(prefix, i, errors) }

internal fun Collection<DatasetContact?>.validate(errors: ValidationErrors, prefix: String = "") =
  forEachIndexed { i, con -> con.validate(prefix, i, errors) }

// Based on App DB vdi_control_*.dataset_organism table
private const val DatasetOrganismAbbrevMaxLength = 20
private const val DatasetOrganismAbbrevMinLength = 3

internal fun Iterable<String?>.validateOrganisms(errors: ValidationErrors, prefix: String = "") {
  forEachIndexed { i, l ->
    if (l.checkNonEmpty(JsonField.ORGANISMS, i, prefix, errors)) {
      l.checkMinLength(JsonField.ORGANISMS, i, prefix, DatasetOrganismAbbrevMinLength, errors)
      l.checkMaxLength(JsonField.ORGANISMS, i, prefix, DatasetOrganismAbbrevMaxLength, errors)
    }
  }
}

internal fun Collection<String?>?.validateProjects(errors: ValidationErrors, prefix: String = "") {
  if (checkNonEmpty(JsonField.PROJECTS, prefix, errors)) {
    forEachIndexed { i, p ->
      if (p.checkNonEmpty(JsonField.PROJECTS, i, prefix, errors)) {
        if (!Options.projects.contains(p)) {
          errors.add((JsonField.PROJECTS + "[$i]").withJPathPrefix(prefix), "unknown or disabled target project")
        }
      }
    }
  }
}


/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Complex Type Validation                                              │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */




/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Error Text Formatting                                                │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */

internal inline fun String.withJPathPrefix(prefix: String) =
  if (prefix.isNotBlank())
    "$prefix.$this"
  else
    this


/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Common Checks                                                        │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */

internal inline fun String.checkMaxLength(name: String, index: Int, prefix: String, max: Int, errors: ValidationErrors) {
  if (length > max)
    errors.add("$name[$index]".withJPathPrefix(prefix), "field must not be longer than $max characters")
}

internal inline fun String.checkMaxLength(name: String, prefix: String, max: Int, errors: ValidationErrors) {
  if (length > max)
    errors.add(name.withJPathPrefix(prefix), "field must not be longer than $max characters")
}

internal inline fun String.checkLength(name: String, prefix: String, min: Int, max: Int, errors: ValidationErrors) {
  checkMinLength(name, prefix, min, errors)
  checkMaxLength(name, prefix, max, errors)
}

internal inline fun String.checkMinLength(name: String, prefix: String, min: Int, errors: ValidationErrors) {
  if (length < min)
    errors.add(name.withJPathPrefix(prefix), "field must be at least $min characters in length")
}

internal inline fun String.checkMinLength(name: String, index: Int, prefix: String, min: Int, errors: ValidationErrors) {
  if (length < min)
    errors.add("$name[$index]".withJPathPrefix(prefix), "field must be at least $min characters in length")
}

@OptIn(ExperimentalContracts::class)
internal inline fun String?.checkNonEmpty(name: String, prefix: String, errors: ValidationErrors): Boolean {
  contract { returns(true) implies (this@checkNonEmpty != null) }

  if (this == null)
    errors.add(name.withJPathPrefix(prefix), "must not be null")
  else if (isBlank())
    errors.add(name.withJPathPrefix(prefix), "must not be blank")
  else
    return true

  return false
}

@OptIn(ExperimentalContracts::class)
internal inline fun String?.checkNonEmpty(name: String, index: Int, prefix: String, errors: ValidationErrors): Boolean {
  contract { returns(true) implies (this@checkNonEmpty != null) }

  if (this == null)
    errors.add("$name[$index]".withJPathPrefix(prefix), "must not be null")
  else if (isBlank())
    errors.add("$name[$index]".withJPathPrefix(prefix), "must not be blank")
  else
    return true

  return false
}

@OptIn(ExperimentalContracts::class)
internal inline fun Collection<*>?.checkNonEmpty(name: String, prefix: String, errors: ValidationErrors): Boolean {
  contract { returns(true) implies (this@checkNonEmpty != null) }

  if (this == null)
    errors.add(name.withJPathPrefix(prefix), "must not be null")
  else if (isEmpty())
    errors.add(name.withJPathPrefix(prefix), "must not be empty")
  else
    return true

  return false
}
