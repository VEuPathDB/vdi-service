package vdi.module.handler.imports.triggers.config

import vdi.components.common.env.*
import vdi.components.common.util.HostAddress
import vdi.components.kafka.KafkaConsumerConfig
import vdi.components.kafka.KafkaConsumerConfigDefaults
import vdi.components.kafka.KafkaOffsetType

internal fun loadConfigFromEnvironment() = loadConfigFromEnvironment(System.getenv())

internal fun loadConfigFromEnvironment(env: Map<String, String>) =
  ImportTriggerHandlerConfig(
    kafkaConfig = loadKafkaConfigFromEnvironment(env),
    s3Bucket    = env.require(EnvKey.S3.BucketName)
  )

internal fun loadKafkaConfigFromEnvironment(env: Map<String, String>) =
  KafkaConfig(
    consumerConfig = KafkaConsumerConfig(
      servers                 = env.require(EnvKey.Kafka.Servers)
                                   .toHostAddresses(),
      fetchMinBytes           = env.optInt(EnvKey.Kafka.Consumer.FetchMinBytes)
                                   ?: KafkaConsumerConfigDefaults.FetchMinBytes,
      groupID                 = env.require(EnvKey.Kafka.Consumer.GroupID),
      heartbeatInterval       = env.optDuration(EnvKey.Kafka.Consumer.HeartbeatInterval)
                                   ?: KafkaConsumerConfigDefaults.HeartbeatInterval,
      sessionTimeout          = env.optDuration(EnvKey.Kafka.Consumer.SessionTimeout)
                                   ?: KafkaConsumerConfigDefaults.SessionTimeout,
      autoOffsetReset         = env.optional(EnvKey.Kafka.Consumer.AutoOffsetReset)
                                   ?.let(KafkaOffsetType::fromString)
                                   ?: KafkaConsumerConfigDefaults.AutoOffsetReset,
      connectionsMaxIdle      = env.optDuration(EnvKey.Kafka.Consumer.ConnectionsMaxIdle)
                                   ?: KafkaConsumerConfigDefaults.ConnectionsMaxIdle,
      defaultAPITimeout       = env.optDuration(EnvKey.Kafka.Consumer.DefaultAPITimeout)
                                   ?: KafkaConsumerConfigDefaults.DefaultAPITimeout,
      enableAutoCommit        = env.optBool(EnvKey.Kafka.Consumer.EnableAutoCommit)
                                   ?: KafkaConsumerConfigDefaults.EnableAutoCommit,
      fetchMaxBytes           = env.optInt(EnvKey.Kafka.Consumer.FetchMaxBytes)
                                   ?: KafkaConsumerConfigDefaults.FetchMaxBytes,
      groupInstanceID         = env.optional(EnvKey.Kafka.Consumer.GroupInstanceID)
                                   ?: KafkaConsumerConfigDefaults.GroupInstanceID,
      maxPollInterval         = env.optDuration(EnvKey.Kafka.Consumer.MaxPollInterval)
                                   ?: KafkaConsumerConfigDefaults.MaxPollInterval,
      maxPollRecords          = env.optInt(EnvKey.Kafka.Consumer.MaxPollRecords)
                                   ?: KafkaConsumerConfigDefaults.MaxPollRecords,
      receiveBufferSizeBytes  = env.optInt(EnvKey.Kafka.Consumer.ReceiveBufferSizeBytes)
                                   ?: KafkaConsumerConfigDefaults.ReceiveBufferSizeBytes,
      requestTimeout          = env.optDuration(EnvKey.Kafka.Consumer.RequestTimeout)
                                   ?: KafkaConsumerConfigDefaults.RequestTimeout,
      sendBufferSizeBytes     = env.optInt(EnvKey.Kafka.Consumer.SendBufferSizeBytes)
                                   ?: KafkaConsumerConfigDefaults.SendBufferSizeBytes,
      autoCommitInterval      = env.optDuration(EnvKey.Kafka.Consumer.AutoCommitInterval)
                                   ?: KafkaConsumerConfigDefaults.AutoCommitInterval,
      clientID                = env.require(EnvKey.Kafka.Consumer.ClientID),
      reconnectBackoffMaxTime = env.optDuration(EnvKey.Kafka.Consumer.ReconnectBackoffMaxTime)
                                   ?: KafkaConsumerConfigDefaults.ReconnectBackoffMaxTime,
      reconnectBackoffTime    = env.optDuration(EnvKey.Kafka.Consumer.ReconnectBackoffTime)
                                   ?: KafkaConsumerConfigDefaults.ReconnectBackoffTime,
      retryBackoffTime        = env.optDuration(EnvKey.Kafka.Consumer.RetryBackoffTime)
                                   ?: KafkaConsumerConfigDefaults.RetryBackoffTime,
      pollDuration            = env.optDuration(EnvKey.Kafka.Consumer.PollDuration)
                                   ?: KafkaConsumerConfigDefaults.PollDuration
    ),
    importTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.ImportTriggers)
                                 ?: KafkaConfigDefaults.ImportTriggerMessageKey,
    importTriggerTopic      = env.optional(EnvKey.Kafka.Topic.ImportTriggers)
                                 ?: KafkaConfigDefaults.ImportTriggerTopic,
    importResultMessageKey  = env.optional(EnvKey.Kafka.MessageKey.ImportResults)
                                 ?: KafkaConfigDefaults.ImportResultMessageKey,
    importResultTopic       = env.optional(EnvKey.Kafka.Topic.ImportResults)
                                 ?: KafkaConfigDefaults.ImportResultTopic
  )


private fun String.toPairSequence() = splitToSequence(',')
  .map { it.toKeyValue() }

private fun String.toHostAddresses() = toPairSequence()
  .map { HostAddress(it.first, it.second.toUShort()) }
  .toList()
  .toTypedArray()

private fun String.toKeyValue(): Pair<String, String> {
  val index = indexOf(':')

  if (index < 0)
    throw IllegalStateException("malformed map in environment variable, no key/value separator character was found")
  if (index < 1)
    throw IllegalStateException("malformed map in environment variable, entry with empty key was found")

  return substring(0, index) to substring(index+1)
}
