package vdi.io.plugin.responses

enum class PluginResponseStatus(val code: UShort) {
  /**
   * Status code returned when the plugin server has successfully processed a
   * request.
   */
  Success(200u),

  /**
   * Status code returned when a plugin script returned one or more basic level
   * data integrity validation errors, and optionally one or more community
   * level data validation errors.
   */
  ValidationError(418u),

  /**
   * Status code returned when a plugin script concludes that the install target
   * is missing one or more prerequisite dependencies required by the given
   * dataset.
   */
  MissingDependencyError(420u),

  /**
   * Status code returned when the plugin server itself encounters an unhandled
   * error.
   */
  ServerError(500u),

  /**
   * Status code returned with a plugin script returns an unexpected error.
   */
  ScriptError(555u),

  /**
   * Illegal state.
   */
  Unrecognized(UShort.MAX_VALUE)
  ;

  companion object {
    fun valueOf(code: Int) =
      code.toUShort().let { entries.firstOrNull { e -> e.code == it } }
        ?: Unrecognized
  }
}