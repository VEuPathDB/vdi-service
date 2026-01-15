package vdi.service.rest.server.inputs

import com.fasterxml.jackson.annotation.JsonProperty
import org.veupathdb.lib.request.validation.*
import kotlin.reflect.KFunction1
import kotlin.reflect.full.findAnnotation
import vdi.core.install.InstallTargetRegistry


/* ┌────────────────────────────────────────────────────────────────────────┐ *\
 * │                                                                        │ *
 * │   Scalar Collection Type Validations                                   │ *
 * │                                                                        │ *
\* └────────────────────────────────────────────────────────────────────────┘ */

fun Collection<String?>.reqEntriesCheckLength(jPath: String, length: IntRange, errors: ValidationErrors) {
  this.forEachIndexed { i, s -> s.reqCheckLength(jPath, i, length.first, length.last, errors) }
}

fun Collection<String?>?.validateProjects(jPath: String, errors: ValidationErrors) {
  requireNonEmpty(jPath, errors) {
    val enabledTargets = InstallTargetRegistry.asSequence()
      .filter { it.enabled }
      .map { it.name }
      .toSet()

    forEachIndexed { i, p ->
      p.require(jPath, i, errors) {
        if (this !in enabledTargets)
          errors.add(jPath..i, "unknown or disabled target project")
      }
    }
  }
}

fun ValidationErrors.readOnlyError(jPath: String) =
  add(jPath, "field is read-only")

/**
 * Validates that the property represented by the given getter has not been
 * altered
 */
fun <T: Any, A: Any> T.requireUnpatched(original: T, jPath: String, errors: ValidationErrors, getter: KFunction1<T, A?>) {
  val new = getter(this)

  if (new != null && new != getter(original))
    errors.readOnlyError(jPath..(getter.findAnnotation<JsonProperty>()?.value ?: getter.name))
}

