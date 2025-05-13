package vdi.lib.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import kotlin.reflect.KClass

class MarkedLogger(mark: String, private val delegate: Logger): Logger by delegate {
  private val marker = MarkerFactory.getMarker(mark)

  constructor(mark: String, name: String): this(mark, LoggerFactory.getLogger(name))

  constructor(mark: String, cls: Class<*>): this(mark, LoggerFactory.getLogger(cls))

  constructor(mark: String, cls: KClass<*>): this(mark, LoggerFactory.getLogger(cls.java))

  override fun isTraceEnabled(): Boolean {
    return delegate.isTraceEnabled(marker)
  }

  override fun trace(msg: String) {
    delegate.trace(marker, msg)
  }

  override fun trace(format: String, arg: Any?) {
    delegate.trace(marker, format, arg)
  }

  override fun trace(format: String, arg1: Any?, arg2: Any?) {
    delegate.trace(marker, format, arg1, arg2)
  }

  override fun trace(format: String, vararg arguments: Any?) {
    delegate.trace(marker, format, *arguments)
  }

  override fun trace(msg: String, t: Throwable) {
    delegate.trace(marker, msg, t)
  }

  override fun isDebugEnabled(): Boolean {
    return isDebugEnabled(marker)
  }

  override fun debug(msg: String) {
    delegate.debug(marker, msg)
  }

  override fun debug(format: String, arg: Any?) {
    delegate.debug(marker, format, arg)
  }

  override fun debug(format: String, arg1: Any?, arg2: Any?) {
    delegate.debug(marker, format, arg1, arg2)
  }

  override fun debug(format: String, vararg arguments: Any?) {
    delegate.debug(marker, format, *arguments)
  }

  override fun debug(msg: String, t: Throwable) {
    delegate.debug(marker, msg, t)
  }

  override fun isInfoEnabled(): Boolean {
    return delegate.isInfoEnabled(marker)
  }

  override fun info(msg: String) {
    delegate.info(marker, msg)
  }

  override fun info(format: String, arg: Any?) {
    delegate.info(marker, format, arg)
  }

  override fun info(format: String, arg1: Any?, arg2: Any?) {
    delegate.info(marker, format, arg1, arg2)
  }

  override fun info(format: String, vararg arguments: Any?) {
    delegate.info(marker, format, *arguments)
  }

  override fun info(msg: String, t: Throwable) {
    delegate.info(marker, msg, t)
  }

  override fun isWarnEnabled(): Boolean {
    return isWarnEnabled(marker)
  }

  override fun warn(msg: String) {
    delegate.warn(marker, msg)
  }

  override fun warn(format: String, arg: Any?) {
    delegate.warn(marker, format, arg)
  }

  override fun warn(format: String, vararg arguments: Any?) {
    delegate.warn(marker, format, *arguments)
  }

  override fun warn(format: String, arg1: Any?, arg2: Any?) {
    delegate.warn(marker, format, arg1, arg2)
  }

  override fun warn(msg: String, t: Throwable) {
    delegate.warn(marker, msg, t)
  }

  override fun isErrorEnabled(): Boolean {
    return delegate.isErrorEnabled(marker)
  }

  override fun error(msg: String) {
    delegate.error(marker, msg)
  }

  override fun error(format: String, arg: Any?) {
    delegate.error(marker, format, arg)
  }

  override fun error(format: String, arg1: Any?, arg2: Any?) {
    delegate.error(marker, format, arg1, arg2)
  }

  override fun error(format: String, vararg arguments: Any?) {
    delegate.error(marker, format, *arguments)
  }

  override fun error(msg: String, t: Throwable) {
    delegate.error(marker, msg, t)
  }
}
