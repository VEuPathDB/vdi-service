package vdi.daemon.pruner

import vdi.config.raw.vdi.daemons.PrunerConfig
import vdi.core.modules.AbortCB

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
