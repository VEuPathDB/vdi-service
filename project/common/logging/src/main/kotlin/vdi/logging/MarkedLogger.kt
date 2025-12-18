package vdi.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.min
import kotlin.reflect.KClass


@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class MarkedLogger(marks: Array<String>, private val delegate: Logger): Logger by delegate {
  private val marks = ArrayList<String>(marks.size)
    .apply { marks.forEach { if (it.isNotBlank()) setMark(it.trim()) } }
    .apply { sort() }
    .toTypedArray()

  private val marker = marks.joinToString(" ", postfix = " - ")

  init {
    if (marks.isEmpty())
      throw IllegalArgumentException("MarkedLogger must be given at least one non-blank log mark")
  }

  constructor(mark: String, name: String): this(arrayOf(mark), LoggerFactory.getLogger(name))

  constructor(mark: String, cls: Class<*>): this(arrayOf(mark), LoggerFactory.getLogger(cls))

  constructor(mark: String, cls: KClass<*>): this(arrayOf(mark), LoggerFactory.getLogger(cls.java))

  constructor(mark: String, logger: Logger): this(arrayOf(mark), logger)

  private fun String.eqPos(): Int {
    for (c in 1 ..< min(length, 5)) {
      if (get(c) == '=')
        return c
    }

    return -1
  }

  private fun MutableList<String>.setMark(mark: String) {
    val pos = mark.eqPos()

    if (pos > -1) {
      val prefix = mark.take(pos+1)

      for (i in indices) {
        if (!get(i).startsWith(prefix))
         continue

        if (get(i).length == prefix.length)
          removeAt(i)
        else
          set(i, mark)

        return
      }
    }

    add(mark)
  }

  fun copy(newMarks: Array<String>) =
    MarkedLogger(marks + newMarks, delegate)

  private fun applyMark(format: String) = marker + format

  private inline fun ifTraceEnabled(fn: () -> Unit) {
    if (delegate.isTraceEnabled)
      fn()
  }

  override fun trace(msg: String) = ifTraceEnabled {
    delegate.trace(applyMark(msg))
  }

  override fun trace(format: String, arg: Any?) = ifTraceEnabled {
    delegate.trace(applyMark(format), arg)
  }

  override fun trace(format: String, arg1: Any?, arg2: Any?) = ifTraceEnabled {
    delegate.trace(applyMark(format), arg1, arg2)
  }

  override fun trace(format: String, vararg arguments: Any?) = ifTraceEnabled {
    delegate.trace(applyMark(format), *arguments)
  }

  override fun trace(msg: String, t: Throwable) = ifTraceEnabled {
    delegate.trace(applyMark(msg), t)
  }

  private inline fun ifDebugEnabled(fn: () -> Unit) {
    if (delegate.isDebugEnabled)
      fn()
  }

  override fun debug(msg: String) = ifDebugEnabled {
    delegate.debug(applyMark(msg))
  }

  override fun debug(format: String, arg: Any?) = ifDebugEnabled {
    delegate.debug(applyMark(format), arg)
  }

  override fun debug(format: String, arg1: Any?, arg2: Any?) = ifDebugEnabled {
    delegate.debug(applyMark(format), arg1, arg2)
  }

  override fun debug(format: String, vararg arguments: Any?) = ifDebugEnabled {
    delegate.debug(applyMark(format), *arguments)
  }

  override fun debug(msg: String, t: Throwable) = ifDebugEnabled {
    delegate.debug(applyMark(msg), t)
  }

  private inline fun ifInfoEnabled(fn: () -> Unit) {
    if (delegate.isInfoEnabled)
      fn()
  }

  override fun info(msg: String) = ifInfoEnabled {
    delegate.info(applyMark(msg))
  }

  override fun info(format: String, arg: Any?) = ifInfoEnabled {
    delegate.info(applyMark(format), arg)
  }

  override fun info(format: String, arg1: Any?, arg2: Any?) = ifInfoEnabled {
    delegate.info(applyMark(format), arg1, arg2)
  }

  override fun info(format: String, vararg arguments: Any?) = ifInfoEnabled {
    delegate.info(applyMark(format), *arguments)
  }

  override fun info(msg: String, t: Throwable) = ifInfoEnabled {
    delegate.info(applyMark(msg), t)
  }

  private inline fun ifWarnEnabled(fn: () -> Unit) {
    if (delegate.isWarnEnabled)
      fn()
  }

  override fun warn(msg: String) = ifWarnEnabled {
    delegate.warn(applyMark(msg))
  }

  override fun warn(format: String, arg: Any?) = ifWarnEnabled {
    delegate.warn(applyMark(format), arg)
  }

  override fun warn(format: String, vararg arguments: Any?) = ifWarnEnabled {
    delegate.warn(applyMark(format), *arguments)
  }

  override fun warn(format: String, arg1: Any?, arg2: Any?) = ifWarnEnabled {
    delegate.warn(applyMark(format), arg1, arg2)
  }

  override fun warn(msg: String, t: Throwable) = ifWarnEnabled {
    delegate.warn(applyMark(msg), t)
  }

  private inline fun ifErrorEnabled(fn: () -> Unit) {
    if (delegate.isErrorEnabled)
      fn()
  }

  override fun error(msg: String) = ifErrorEnabled {
    delegate.error(applyMark(msg))
  }

  override fun error(format: String, arg: Any?) = ifErrorEnabled {
    delegate.error(applyMark(format), arg)
  }

  override fun error(format: String, arg1: Any?, arg2: Any?) = ifErrorEnabled {
    delegate.error(applyMark(format), arg1, arg2)
  }

  override fun error(format: String, vararg arguments: Any?) = ifErrorEnabled {
    delegate.error(applyMark(format), *arguments)
  }

  override fun error(msg: String, t: Throwable) = ifErrorEnabled {
    delegate.error(applyMark(msg), t)
  }
}
