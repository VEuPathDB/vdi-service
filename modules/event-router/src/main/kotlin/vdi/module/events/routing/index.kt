package vdi.module.events.routing

import vdi.module.events.routing.config.EventRouterConfig
import vdi.module.events.routing.config.loadConfigFromEnvironment

/**
 * Constructs a new event router from the given configuration.
 *
 * @param config Configuration for the returned [EventRouter] module.
 *
 * @return a new [EventRouter] instance.
 */
fun EventRouter(config: EventRouterConfig = loadConfigFromEnvironment()): EventRouter = EventRouterImpl(config)