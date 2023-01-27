package vdi.conf

/**
 * VDI Handler Service Root Configuration
 *
 * This configuration object is a global container for the configuration values
 * set on the environment for the VDI handler service.
 */
object Configuration {

  /**
   * HTTP server specific configuration values.
   */
  val serverConfiguration = parseServerConfig()

  /**
   * Database connection configuration values.
   */
  val databaseConfigurations = parseDatabaseConfigs()
}

