package vdi.service.rest.server.inputs

import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1
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

internal fun String?.cleanup(emptyToNull: Boolean = true) =
  this?.trim()?.run { if (emptyToNull && isEmpty()) null else this }

@Suppress("UNCHECKED_CAST")
internal inline fun <T: Any, O: Any> T.cleanup(getter: KFunction0<O?>, fn: (O?) -> O?) {
  val setter = getter.name.replaceFirst('g', 's')
    .let { setter -> this::class.declaredMemberFunctions.first { it.name == setter } }
    .let { it as KFunction2<T, O, Unit> }

  fn(getter())?.also { setter(this, it) }
}


@Suppress("UNCHECKED_CAST")
internal fun <I: Any> Any.cleanup(property: String, fn: (I?) -> I?) {
  val gName: String
  val sName: String

  with(property.substring(0, 1).uppercase() + property.substring(1)) {
    gName = "get$this"
    sName = "set$this"
  }

  var getter: KFunction1<Any, I?>? = null
  var setter: KFunction2<Any, I?, Unit>? = null

  for (f in this::class.declaredMemberFunctions) {
    if (f.name == gName)
      getter = f as KFunction1<Any, I?>
    else if (f.name == sName)
      setter = f as KFunction2<Any, I?, Unit>
    if (getter != null && setter != null)
      break
  }

  fn(requireFunc(getter, gName).call(this))
    ?.also { requireFunc(setter, sName).call(this, it) }
}

internal fun Any.cleanupString(property: String) {
  cleanup<String>(property) { it?.trim() }
}

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


private fun <T> Any.requireFunc(func: T?, name: String) =
  func ?: throw IllegalStateException("${this::class.simpleName} does not have a member method $name")