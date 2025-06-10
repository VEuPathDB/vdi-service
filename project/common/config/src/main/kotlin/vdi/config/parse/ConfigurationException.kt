package vdi.config.parse

import java.lang.RuntimeException

class ConfigurationException: RuntimeException {
  constructor(message: String): super("invalid configuration: $message")
  constructor(cause: Throwable): super("invalid configuration", cause)
}