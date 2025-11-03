@file:JvmName("InputCleanupUtils")
package vdi.service.rest.server.inputs

import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction2
import kotlin.reflect.full.declaredMemberFunctions

/**
 * Applies the given cleanup function to the values in the list provided by
 * [getter] before replacing the original list with a new list containing the
 * non-null cleaned values by way of the parent object's matching 'setter'
 * method.
 *
 * Values for which the given cleanup function maps to `null` will be omitted
 * from the new list.
 *
 * @receiver Parent object of the given [getter] that will be used in calls to
 * the getter as well as its matching setter.
 *
 * @param getter Getter method for the list property to clean.
 *
 * @param fn Cleanup function that will be applied to each value in the list
 * returned by getter.
 */
internal fun <T: Any, I: Any> T.cleanupList(getter: KFunction0<List<I?>?>, fn: (I?) -> I?) =
  cleanup(getter) { it?.mapNotNull(fn)?.ifEmpty { emptyList() } ?: emptyList() }

/**
 * Applies the given cleanup function to the values in the list provided by
 * [getter] before replacing the original list with a new list containing the
 * distinct, non-null cleaned values by way of the parent object's matching
 * 'setter' method.
 *
 * Note: Types generated from RAML specifications do not implement [equals] or
 * [hashCode], and thus cannot be meaningfully tested to ensure uniqueness.  For
 * those types, the `distinct` operation should be performed _after_ conversion
 * to a comparable type.
 *
 * @receiver Parent object of the given [getter] that will be used in calls to
 * the getter as well as its matching setter.
 *
 * @param getter Getter method for the list property to clean.
 *
 * @param fn Cleanup function that will be applied to each value in the list
 * returned by [getter].
 */
internal fun <T: Any, I: Any> T.cleanupDistinctList(getter: KFunction0<List<I?>?>, fn: (I?) -> I?) =
  cleanup(getter) {
    it?.asSequence()
      ?.mapNotNull(fn)
      ?.distinct()
      ?.toList()
      ?.ifEmpty { emptyList() }
      ?: emptyList()
  }

/**
 * Trims whitespaces from the beginning and end of the input string, if it is
 * not `null`, returning either the trimmed form or `null`.
 *
 * Optionally, converts empty strings to `null`.  This is the default behavior.
 *
 * @receiver Nullable string value to trim.
 *
 * @param emptyToNull Whether the input string value should be replaced with
 * `null` if it is empty after being trimmed.
 *
 * @return Either the input string, if present, with the whitespace trimmed from
 * both the right and left sides, or `null` if the receiver value was `null` or
 * if [emptyToNull] is `true` and the receiver value trimmed down to an empty
 * string.
 */
internal fun String?.cleanup(emptyToNull: Boolean = true) =
  this?.trim()?.run { if (emptyToNull && isEmpty()) null else this }

/**
 * Applies an arbitrary 'cleanup' transform function to the value returned by
 * the provided [getter], then sets the cleaned value back to the receiver
 * object using the property's matching setter function.
 *
 * The setter function is assumed to exist with the same name as the getter but
 * with the prefix "get" replaced with "set".
 *
 * @receiver Parent object to which the property and [getter] belong.  This
 * value is used when retrieving and calling the setter function.
 *
 * @param getter Property getter method reference.
 *
 * @param fn "Cleanup" transform function, whose output will be used to replace
 * the original property value.
 */
@Suppress("UNCHECKED_CAST")
internal inline fun <T: Any, O: Any> T.cleanup(getter: KFunction0<O?>, fn: (O?) -> O?) {
  val setter = getter.name.replaceFirst('g', 's')
    .let { setter -> this::class.declaredMemberFunctions.first { it.name == setter } }
    .let { it as KFunction2<T, O, Unit> }

  fn(getter())?.also { setter(this, it) }
}

/**
 * Ensures the property of the receiver instance, represented by the given
 * [getter] is not `null`, setting the property to the given default [value] if
 * it is.
 *
 * @receiver Parent object to which the target property and [getter] belong.
 *
 * @param getter Getter method used to retrieve the current property value.
 *
 * @param value Default/fallback value to set if the current instance property
 * is `null`.
 */
internal fun <T: Any, V: Any> T.ensureNotNull(getter: KFunction0<V?>, value: V) =
  cleanup(getter) { it ?: value }

/**
 * Trim and replace the target string property if it is not `null`.  If
 * [nullOutBlanks] is `true`, blank strings will be replaced with `null`
 * values.
 *
 * This function reflectively finds the setter for the target property based on
 * the given getter method.
 *
 * @receiver Container object to which the given [getter] belongs.
 *
 * @param getter String getter method used to get the value that will be cleaned
 * and replaced.
 *
 * @param nullOutBlanks Whether blank string values should be replaced with
 * `null` values.  This should not be used for situations where a blank string
 * has a specific meaning that differs from a `null` value.
 */
internal fun <T: Any> T.cleanupString(getter: KFunction0<String?>, nullOutBlanks: Boolean = true) =
  cleanup(getter) {
    it?.trim()
      ?.run { if (nullOutBlanks && isEmpty()) null else this }
  }
