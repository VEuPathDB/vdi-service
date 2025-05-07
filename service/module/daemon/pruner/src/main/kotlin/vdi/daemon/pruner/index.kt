package vdi.daemon.pruner

import vdi.lib.config.vdi.daemons.PrunerConfig
import vdi.lib.modules.AbortCB

/**
 * Constructs a new [PrunerModule] instance with the given (or defaulted)
 * configuration.
 *
 * @param config Module configuration
 *
 * @return A new [PrunerModule] instance.
 */
fun PrunerModule(config: PrunerConfig?, abortCB: AbortCB): PrunerModule =
  PrunerModule(PrunerModuleConfig(config), abortCB)

/**
 * Constructs a new [PrunerModule] instance with the given (or defaulted)
 * configuration.
 *
 * @param config Module configuration
 *
 * @return A new [PrunerModule] instance.
 */
fun PrunerModule(config: PrunerModuleConfig, abortCB: AbortCB): PrunerModule =
  PrunerModuleImpl(config, abortCB)
