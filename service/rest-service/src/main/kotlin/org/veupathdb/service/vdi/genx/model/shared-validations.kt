@file:Suppress("NOTHING_TO_INLINE")
package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetContact
import org.veupathdb.service.vdi.generated.model.DatasetHyperlink
import org.veupathdb.service.vdi.generated.model.DatasetPublication
import org.veupathdb.service.vdi.generated.model.JsonField
import org.veupathdb.service.vdi.util.ValidationErrors
import vdi.component.db.app.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Error Text Formatting                                                │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */

private inline fun String.withPrefix(prefix: String) =
  if (prefix.isNotBlank())
    "$prefix.$this"
  else
    this


/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Common Checks                                                        │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */

private inline fun String.checkMaxLength(name: String, index: Int, prefix: String, max: Int, errors: ValidationErrors) {
  if (length > max)
    errors.add("$name[$index]".withPrefix(prefix), "field must not be longer than $max characters")
}

private inline fun String.checkMaxLength(name: String, prefix: String, max: Int, errors: ValidationErrors) {
  if (length > max)
    errors.add(name.withPrefix(prefix), "field must not be longer than $max characters")
}

private inline fun String.checkMinLength(name: String, prefix: String, min: Int, errors: ValidationErrors) {
  if (length < min)
    errors.add(name.withPrefix(prefix), "field must be at least $min characters in length")
}

private inline fun String.checkMinLength(name: String, index: Int, prefix: String, min: Int, errors: ValidationErrors) {
  if (length < min)
    errors.add("$name[$index]".withPrefix(prefix), "field must be at least $min characters in length")
}

@OptIn(ExperimentalContracts::class)
private inline fun String?.checkNonEmpty(name: String, prefix: String, errors: ValidationErrors): Boolean {
  contract { returns(true) implies (this@checkNonEmpty != null) }

  if (this == null)
    errors.add(name.withPrefix(prefix))
  else if (isBlank())
    errors.add(name.withPrefix(prefix), "entries must not be blank")
  else
    return true

  return false
}

@OptIn(ExperimentalContracts::class)
private inline fun String?.checkNonEmpty(name: String, index: Int, prefix: String, errors: ValidationErrors): Boolean {
  contract { returns(true) implies (this@checkNonEmpty != null) }

  if (this == null)
    errors.add("$name[$index]".withPrefix(prefix), "entries must not be null")
  else if (isBlank())
    errors.add("$name[$index]".withPrefix(prefix), "entries must not be blank")
  else
    return true

  return false
}


/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Complex Type Validations                                             │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */

internal fun Collection<DatasetPublication>.validate(errors: ValidationErrors, prefix: String = "") {
  if (isEmpty())
    return

  var hasPrimary = false

  forEachIndexed { i, pub ->
    pub.validate(prefix, i, errors)

    if (hasPrimary)
      errors.add("${JsonField.PUBLICATIONS}[$i]".withPrefix(prefix), "only one publication may be marked as primary")
    else
      hasPrimary = true
  }

  if (!hasPrimary)
    errors.add(JsonField.PUBLICATIONS.withPrefix(prefix), "one publication must be marked as primary")
}

internal fun Collection<DatasetHyperlink?>.validate(errors: ValidationErrors, prefix: String = "") {
  forEachIndexed { i, link -> link.validate(prefix, i, errors) }
}

internal fun Collection<DatasetContact?>.validate(errors: ValidationErrors, prefix: String = "") {
  forEachIndexed { i, con -> con.validate(prefix, i, errors) }
}

// Based on App DB vdi_control_*.dataset_organism table
private const val DatasetOrganismAbbrevMaxLength = 20
private const val DatasetOrganismAbbrevMinLength = 3

internal fun Collection<String?>.validateOrganisms(errors: ValidationErrors, prefix: String = "") {
  forEachIndexed { i, l ->
    if (l.checkNonEmpty(JsonField.ORGANISMS, i, prefix, errors)) {
      l.checkMinLength(JsonField.ORGANISMS, i, prefix, DatasetOrganismAbbrevMinLength, errors)
      l.checkMaxLength(JsonField.ORGANISMS, i, prefix, DatasetOrganismAbbrevMaxLength, errors)
    }
  }
}


/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Simple Type Validations                                              │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */

const val DatasetMetaMinNameLength = 3

internal fun String?.validateName(errors: ValidationErrors, prefix: String = "") {
  if (checkNonEmpty(JsonField.NAME, prefix, errors)) {
    checkMinLength(JsonField.NAME, prefix, DatasetMetaMinNameLength, errors)
    checkMaxLength(JsonField.NAME, prefix, DatasetMetaMaxNameLength, errors)
  }
}

internal fun String.validateSummary(errors: ValidationErrors, prefix: String = "") =
  checkMaxLength("summary", prefix, DatasetMetaMaxSummaryFieldLength, errors)

internal fun String.validateShortName(errors: ValidationErrors, prefix: String = "") =
  checkMaxLength("shortName", prefix, DatasetMetaMaxShortNameLength, errors)

internal fun String.validateShortAttribution(errors: ValidationErrors, prefix: String = "") =
  checkMaxLength("shortAttribution", prefix, DatasetMetaMaxShortAttributionLength, errors)

internal fun String.validateCategory(errors: ValidationErrors, prefix: String = "") =
  checkMaxLength("category", prefix, DatasetMetaMaxCategoryLength, errors)

private const val DatasetOriginMaxLength = 256
private const val DatasetOriginMinLength = 3

internal fun String?.validateOrigin(errors: ValidationErrors, prefix: String = "") {
  if (checkNonEmpty("origin", prefix, errors)) {
    checkMinLength("origin", prefix, DatasetOriginMinLength, errors)
    checkMaxLength("origin", prefix, DatasetOriginMaxLength, errors)
  }
}
