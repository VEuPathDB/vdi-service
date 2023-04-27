package vdi.module.events.routing.config

import org.veupathdb.vdi.lib.common.env.EnvKey
import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.require
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfig
import org.veupathdb.vdi.lib.rabbit.RabbitMQConfig

/**
 * Loads the [EventRouterConfig] from environment variables.
 *
 * @return The loaded `EventRouterConfig` instance.
 */
internal fun loadConfigFromEnvironment() = loadConfigFromEnvironment(System.getenv())

/**
 * Loads the [EventRouterConfig] from the given map of environment variables.
 *
 * @param env Map of environment variables from which the config should be
 * loaded.
 *
 * @return The loaded `EventRouterConfig` instance.
 */
internal fun loadConfigFromEnvironment(env: Environment) =
  EventRouterConfig(
    rabbitConfig = RabbitMQConfig(env),
    s3Bucket     = env.require(EnvKey.S3.BucketName),
    kafkaConfig  = KafkaRouterConfig(env)
  )
