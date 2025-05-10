package vdi.lib.logging

import org.slf4j.Logger
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

class DatasetContextLogger(
  datasetID: DatasetID,
  ownerID: UserID,
  private val logger: Logger,
): Logger by logger {
  private val prefix = "$datasetID/$ownerID: "

  override fun trace(msg: String) {
    if (logger.isTraceEnabled) {
      logger.trace(prefix + msg)
    }
  }

  override fun trace(format: String, arg: Any?) {
    if (logger.isTraceEnabled) {
      logger.trace(prefix + format, arg)
    }
  }

  override fun trace(msg: String, t: Throwable) {
    if (logger.isTraceEnabled) {
      logger.trace(prefix + msg, t)
    }
  }

  override fun trace(format: String, arg1: Any?, arg2: Any?) {
    if (logger.isTraceEnabled) {
      logger.trace(prefix + format, arg1, arg2)
    }
  }

  override fun trace(format: String, vararg arguments: Any?) {
    if (logger.isTraceEnabled) {
      logger.trace(prefix + format, *arguments)
    }
  }

  override fun debug(msg: String) {
    if (logger.isDebugEnabled) {
      logger.debug(prefix + msg)
    }
  }

  override fun debug(format: String, arg: Any?) {
    if (logger.isDebugEnabled) {
      logger.debug(prefix + format, arg)
    }
  }

  override fun debug(msg: String, t: Throwable) {
    if (logger.isDebugEnabled) {
      logger.debug(prefix + msg, t)
    }
  }

  override fun debug(format: String, arg1: Any?, arg2: Any?) {
    if (logger.isDebugEnabled) {
      logger.debug(prefix + format, arg1, arg2)
    }
  }

  override fun debug(format: String, vararg arguments: Any?) {
    if (logger.isDebugEnabled) {
      logger.debug(prefix + format, *arguments)
    }
  }

  override fun info(msg: String) {
    if (logger.isInfoEnabled) {
      logger.info(prefix + msg)
    }
  }

  override fun info(format: String, arg: Any?) {
    if (logger.isInfoEnabled) {
      logger.info(prefix + format, arg)
    }
  }

  override fun info(msg: String, t: Throwable) {
    if (logger.isInfoEnabled) {
      logger.info(prefix + msg, t)
    }
  }

  override fun info(format: String, arg1: Any?, arg2: Any?) {
    if (logger.isInfoEnabled) {
      logger.info(prefix + format, arg1, arg2)
    }
  }

  override fun info(format: String, vararg arguments: Any?) {
    if (logger.isInfoEnabled) {
      logger.info(prefix + format, *arguments)
    }
  }

  override fun warn(msg: String) {
    if (logger.isWarnEnabled) {
      logger.warn(prefix + msg)
    }
  }

  override fun warn(format: String, arg: Any?) {
    if (logger.isWarnEnabled) {
      logger.warn(prefix + format, arg)
    }
  }

  override fun warn(msg: String, t: Throwable) {
    if (logger.isWarnEnabled) {
      logger.warn(prefix + msg, t)
    }
  }

  override fun warn(format: String, arg1: Any?, arg2: Any?) {
    if (logger.isWarnEnabled) {
      logger.warn(prefix + format, arg1, arg2)
    }
  }

  override fun warn(format: String, vararg arguments: Any?) {
    if (logger.isWarnEnabled) {
      logger.warn(prefix + format, *arguments)
    }
  }

  override fun error(msg: String) {
    if (logger.isErrorEnabled) {
      logger.error(prefix + msg)
    }
  }

  override fun error(format: String, arg: Any?) {
    if (logger.isErrorEnabled) {
      logger.error(prefix + format, arg)
    }
  }

  override fun error(msg: String, t: Throwable) {
    if (logger.isErrorEnabled) {
      logger.error(prefix + msg, t)
    }
  }

  override fun error(format: String, arg1: Any?, arg2: Any?) {
    if (logger.isErrorEnabled) {
      logger.error(prefix + format, arg1, arg2)
    }
  }

  override fun error(format: String, vararg arguments: Any?) {
    if (logger.isErrorEnabled) {
      logger.error(prefix + format, *arguments)
    }
  }
}
