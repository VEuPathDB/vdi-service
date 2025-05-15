package vdi.lib.db.app.model

/**
 * Dataset Installation Status
 *
 * Status value for the various states that a dataset installation could be in.
 *
 * @since 1.0.0
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
enum class InstallStatus(val value: String) {

  /**
   * Dataset installation is currently in progress.
   */
  Running("running"),

  /**
   * Dataset installation is successfully completed.
   */
  Complete("complete"),

  /**
   * Dataset installation could not be completed due to a validation failure.
   */
  FailedValidation("failed-validation"),

  /**
   * Dataset installation failed due to an internal or unexpected error.
   */
  FailedInstallation("failed-installation"),

  /**
   * Dataset installation could not be completed due to one or more missing
   * dependencies.
   */
  MissingDependency("missing-dependency"),

  /**
   * Dataset has been marked as ready for reinstallation.
   */
  ReadyForReinstall("ready-for-reinstall"),
  ;

  companion object {
    @JvmStatic
    fun fromString(value: String): InstallStatus {
      for (enum in entries)
        if (enum.value == value)
          return enum

      throw IllegalArgumentException("unrecognized InstallStatus value: $value")
    }
  }
}

