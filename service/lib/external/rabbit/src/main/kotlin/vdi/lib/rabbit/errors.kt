package vdi.lib.rabbit

internal class RabbitConnectionClosedError(msg: String = "rabbitmq connection is closed") : Exception(msg)
