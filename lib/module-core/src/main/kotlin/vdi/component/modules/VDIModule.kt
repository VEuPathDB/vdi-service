package vdi.component.modules

/**
 * VDI Service Module
 *
 * Represents a sub-service of the VDI system.  This interface provides methods
 * for starting and stopping the service module.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
interface VDIModule {

  val name: String

  /**
   * Starts the service module.
   *
   * Calling this method more than once has no additional effect.
   */
  suspend fun start()

  /**
   * Stops the service module.
   *
   * Once a service module has been stopped, it cannot be restarted.
   */
  suspend fun stop()
}

