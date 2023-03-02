package vdi.components.common

/**
 * Inline string wrapper used to prevent passwords or other secrets from being
 * printed to the process log.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
@JvmInline
value class SecretString(val value: String) {

  /**
   * Overridden [toString] that hides the wrapped value.
   */
  override fun toString() = "***"
}