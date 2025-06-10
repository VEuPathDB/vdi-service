package vdi.daemon.events.routing

import vdi.core.config.vdi.VDIConfig
import vdi.core.modules.AbortCB

/**
 * Constructs a new event router from the given configuration.
 *
 * @param config Configuration for the returned [EventRouter] module.
 *
 * @return a new [EventRouter] instance.
 */
fun EventRouter(config: VDIConfig, abortCB: AbortCB): EventRouter =
  EventRouterImpl(EventRouterConfig(config), abortCB)

/**
 * Constructs a new event router from the given configuration.
 *
 * @param config Configuration for the returned [EventRouter] module.
 *
 * @return a new [EventRouter] instance.
 */
fun EventRouter(config: EventRouterConfig, abortCB: AbortCB): EventRouter =
  EventRouterImpl(config, abortCB)
