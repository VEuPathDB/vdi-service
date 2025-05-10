package vdi.lib.err

class StartupException : Exception {
  constructor(message: String): super(message)
  constructor(message: String, cause: Throwable): super(message, cause)
  constructor(cause: Throwable): super("service failed to start", cause)
}
