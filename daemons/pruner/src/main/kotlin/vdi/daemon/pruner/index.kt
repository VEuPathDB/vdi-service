package vdi.daemon.pruner

/**
 * Constructs a new [PrunerModule] instance with the given (or defaulted)
 * configuration.
 *
 * @param config Module configuration
 *
 * @return A new [PrunerModule] instance.
 */
fun PrunerModule(config: PrunerModuleConfig = PrunerModuleConfig()): PrunerModule =
  PrunerModuleImpl(config)
