package vdi.config.parse

import java.lang.RuntimeException

class ConfigurationException(message: String): RuntimeException("invalid configuration: $message")