package vdi.module.events.routing

/**
 * Constructs a new event router from the given configuration.
 *
 * @param config Configuration for the returned [EventRouter] module.
 *
 * @return a new [EventRouter] instance.
 */
fun EventRouter(config: EventRouterConfig = EventRouterConfig()): EventRouter = EventRouterImpl(config)
