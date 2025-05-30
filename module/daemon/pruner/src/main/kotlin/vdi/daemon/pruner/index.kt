package vdi.daemon.pruner

import vdi.lib.config.vdi.daemons.PrunerConfig
import vdi.lib.modules.AbortCB

/**
 * Constructs a new [DatasetPruner] instance with the given (or defaulted)
 * configuration.
 *
 * @param config Module configuration
 *
 * @return A new [DatasetPruner] instance.
 */
fun DatasetPruner(config: PrunerConfig?, abortCB: AbortCB): DatasetPruner =
  DatasetPruner(DatasetPrunerConfig(config), abortCB)

/**
 * Constructs a new [DatasetPruner] instance with the given (or defaulted)
 * configuration.
 *
 * @param config Module configuration
 *
 * @return A new [DatasetPruner] instance.
 */
fun DatasetPruner(config: DatasetPrunerConfig, abortCB: AbortCB): DatasetPruner =
  DatasetPrunerImpl(config, abortCB)
