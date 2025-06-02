package vdi.core.rabbit

internal class RabbitConnectionClosedError(msg: String = "rabbitmq connection is closed") : Exception(msg)
