package vdi.core.db.app.model;

public enum InstallStatus {

  /**
   * Dataset installation is currently in progress.
   */
  RUNNING("running"),

  /**
   * Dataset installation is successfully completed.
   */
  COMPLETE("complete"),

  /**
   * Dataset installation could not be completed due to a validation failure.
   */
  FAILED_VALIDATION("failed-validation"),

  /**
   * Dataset installation failed due to an internal or unexpected error.
   */
  FAILED_INSTALLATION("failed-installation"),

  /**
   * Dataset installation could not be completed due to one or more missing
   * dependencies.
   */
  MISSING_DEPENDENCY("missing-dependency"),

  /**
   * Dataset has been marked as ready for reinstallation.
   */
  READY_FOR_REINSTALL("ready-for-reinstall"),
  ;


  public final String value;

  InstallStatus(String value) {
    this.value = value;
  }

  public static InstallStatus fromString(String value) {
    for (var e : values())
      if (e.value.equals(value))
        return e;

    throw new IllegalArgumentException("unrecognized InstallStatus value: " + value);
  }
}
