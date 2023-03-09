package vdi.components.common.env

/**
 * Global Registry of all environment variables declared by the VDI project.
 *
 * This listing does not include environment variables that are declared by the
 * container core library.
 */
object EnvKey {

  object CacheDB {
    const val Host     = "CACHE_DB_HOST"
    const val Port     = "CACHE_DB_PORT"
    const val Name     = "CACHE_DB_NAME"
    const val Username = "CACHE_DB_USERNAME"
    const val Password = "CACHE_DB_PASSWORD"
    const val PoolSize = "CACHE_DB_POOL_SIZE"
  }

  object Kafka {

    const val Servers = "KAFKA_SERVERS"

    object Consumer {
      const val AutoCommitInterval      = "KAFKA_CONSUMER_AUTO_COMMIT_INTERVAL"
      const val AutoOffsetReset         = "KAFKA_CONSUMER_AUTO_OFFSET_RESET"
      const val ClientID                = "KAFKA_CONSUMER_CLIENT_ID"
      const val ConnectionsMaxIdle      = "KAFKA_CONSUMER_CONNECTIONS_MAX_IDLE"
      const val DefaultAPITimeout       = "KAFKA_CONSUMER_DEFAULT_API_TIMEOUT"
      const val EnableAutoCommit        = "KAFKA_CONSUMER_ENABLE_AUTO_COMMIT"
      const val FetchMaxBytes           = "KAFKA_CONSUMER_FETCH_MAX_BYTES"
      const val FetchMinBytes           = "KAFKA_CONSUMER_FETCH_MIN_BYTES"
      const val GroupID                 = "KAFKA_CONSUMER_GROUP_ID"
      const val GroupInstanceID         = "KAFKA_CONSUMER_GROUP_INSTANCE_ID"
      const val HeartbeatInterval       = "KAFKA_CONSUMER_HEARTBEAT_INTERVAL"
      const val MaxPollInterval         = "KAFKA_CONSUMER_MAX_POLL_INTERVAL"
      const val MaxPollRecords          = "KAFKA_CONSUMER_MAX_POLL_RECORDS"
      const val PollDuration            = "KAFKA_CONSUMER_POLL_DURATION"
      const val ReceiveBufferSizeBytes  = "KAFKA_CONSUMER_RECEIVE_BUFFER_SIZE_BYTES"
      const val ReconnectBackoffMaxTime = "KAFKA_CONSUMER_RECONNECT_BACKOFF_MAX_TIME"
      const val ReconnectBackoffTime    = "KAFKA_CONSUMER_RECONNECT_BACKOFF_TIME"
      const val RequestTimeout          = "KAFKA_CONSUMER_REQUEST_TIMEOUT"
      const val RetryBackoffTime        = "KAFKA_CONSUMER_RETRY_BACKOFF_TIME"
      const val SendBufferSizeBytes     = "KAFKA_CONSUMER_SEND_BUFFER_SIZE_BYTES"
      const val SessionTimeout          = "KAFKA_CONSUMER_SESSION_TIMEOUT"
    }

    object Producer {
      const val BatchSize               = "KAFKA_PRODUCER_BATCH_SIZE"
      const val BufferMemoryBytes       = "KAFKA_PRODUCER_BUFFER_MEMORY_BYTES"
      const val ClientID                = "KAFKA_PRODUCER_CLIENT_ID"
      const val CompressionType         = "KAFKA_PRODUCER_COMPRESSION_TYPE"
      const val ConnectionsMaxIdle      = "KAFKA_PRODUCER_CONNECTIONS_MAX_IDLE"
      const val DeliveryTimeout         = "KAFKA_PRODUCER_DELIVERY_TIMEOUT"
      const val LingerTime              = "KAFKA_PRODUCER_LINGER_TIME"
      const val MaxBlockingTimeout      = "KAFKA_PRODUCER_MAX_BLOCKING_TIMEOUT"
      const val MaxRequestSizeBytes     = "KAFKA_PRODUCER_MAX_REQUEST_SIZE_BYTES"
      const val ReceiveBufferSizeBytes  = "KAFKA_PRODUCER_RECEIVE_BUFFER_SIZE_BYTES"
      const val ReconnectBackoffMaxTime = "KAFKA_PRODUCER_RECONNECT_BACKOFF_MAX_TIME"
      const val ReconnectBackoffTime    = "KAFKA_PRODUCER_RECONNECT_BACKOFF_TIME"
      const val RequestTimeout          = "KAFKA_PRODUCER_REQUEST_TIMEOUT"
      const val RetryBackoffTime        = "KAFKA_PRODUCER_RETRY_BACKOFF_TIME"
      const val SendBufferSizeBytes     = "KAFKA_PRODUCER_SEND_BUFFER_SIZE_BYTES"
      const val SendRetries             = "KAFKA_PRODUCER_SEND_RETRIES"
    }

    object Topic {
      const val HardDeleteTriggers = "KAFKA_TOPIC_HARD_DELETE_TRIGGERS"
      const val ImportResults      = "KAFKA_TOPIC_IMPORT_RESULTS"
      const val ImportTriggers     = "KAFKA_TOPIC_IMPORT_TRIGGERS"
      const val InstallTriggers    = "KAFKA_TOPIC_INSTALL_TRIGGERS"
      const val ShareTriggers      = "KAFKA_TOPIC_SHARE_TRIGGERS"
      const val SoftDeleteTriggers = "KAFKA_TOPIC_SOFT_DELETE_TRIGGERS"
      const val UpdateMetaTriggers = "KAFKA_TOPIC_UPDATE_META_TRIGGERS"
    }

    object MessageKey {
      const val HardDeleteTriggers = "KAFKA_MESSAGE_KEY_HARD_DELETE_TRIGGERS"
      const val ImportResults      = "KAFKA_MESSAGE_KEY_IMPORT_RESULTS"
      const val ImportTriggers     = "KAFKA_MESSAGE_KEY_IMPORT_TRIGGERS"
      const val InstallTriggers    = "KAFKA_MESSAGE_KEY_INSTALL_TRIGGERS"
      const val ShareTriggers      = "KAFKA_MESSAGE_KEY_SHARE_TRIGGERS"
      const val SoftDeleteTriggers = "KAFKA_MESSAGE_KEY_SOFT_DELETE_TRIGGERS"
      const val UpdateMetaTriggers = "KAFKA_MESSAGE_KEY_UPDATE_META_TRIGGERS"
    }
  }

  object Rabbit {
    const val ConnectionName  = "GLOBAL_RABBIT_CONNECTION_NAME"
    const val Host            = "GLOBAL_RABBIT_HOST"
    const val Password        = "GLOBAL_RABBIT_PASSWORD"
    const val PollingInterval = "GLOBAL_RABBIT_VDI_POLLING_INTERVAL"
    const val Port            = "GLOBAL_RABBIT_PORT"
    const val Username        = "GLOBAL_RABBIT_USERNAME"

    object Exchange {
      const val Arguments  = "GLOBAL_RABBIT_VDI_EXCHANGE_ARGUMENTS"
      const val AutoDelete = "GLOBAL_RABBIT_VDI_EXCHANGE_AUTO_DELETE"
      const val Durable    = "GLOBAL_RABBIT_VDI_EXCHANGE_DURABLE"
      const val Name       = "GLOBAL_RABBIT_VDI_EXCHANGE_NAME"
      const val Type       = "GLOBAL_RABBIT_VDI_EXCHANGE_TYPE"
    }

    object Queue {
      const val Arguments  = "GLOBAL_RABBIT_VDI_QUEUE_ARGUMENTS"
      const val AutoDelete = "GLOBAL_RABBIT_VDI_QUEUE_AUTO_DELETE"
      const val Exclusive  = "GLOBAL_RABBIT_VDI_QUEUE_EXCLUSIVE"
      const val Durable    = "GLOBAL_RABBIT_VDI_QUEUE_DURABLE"
      const val Name       = "GLOBAL_RABBIT_VDI_QUEUE_NAME"
    }

    object Routing {
      const val Key       = "GLOBAL_RABBIT_VDI_ROUTING_KEY"
      const val Arguments = "GLOBAL_RABBIT_VDI_ROUTING_ARGUMENTS"
    }
  }

  object S3 {
    const val AccessToken = "S3_ACCESS_TOKEN"
    const val BucketName  = "S3_BUCKET_NAME"
    const val Host        = "S3_HOST"
    const val Port        = "S3_PORT"
    const val SecretKey   = "S3_SECRET_KEY"
    const val UseHTTPS    = "S3_USE_HTTPS"
  }
}