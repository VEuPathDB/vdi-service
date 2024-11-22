package vdi.component.env

import org.veupathdb.vdi.lib.common.env.EnvKey

object EnvKey {
  inline val Admin get() = EnvKey.Admin

  inline val AppDB get() = EnvKey.AppDB

  inline val LDAP get() = EnvKey.LDAP

  object CacheDB {

    /**
     * Type: String
     * Required: yes
     */
    const val Host = "CACHE_DB_HOST"

    /**
     * Type: UShort
     * Required: no
     */
    const val Port = "CACHE_DB_PORT"

    /**
     * Type: String
     * Required: yes
     */
    const val Name = "CACHE_DB_NAME"

    /**
     * Type: String
     * Required: yes
     */
    const val Username = "CACHE_DB_USERNAME"

    /**
     * Type: String
     * Required: yes
     */
    const val Password = "CACHE_DB_PASSWORD"

    /**
     * Type: UByte
     * Required: no
     */
    const val PoolSize = "CACHE_DB_POOL_SIZE"
  }

  /**
   * Handler Environment Key Components.
   *
   * ```
   * PLUGIN_HANDLER_<NAME>_NAME
   * PLUGIN_HANDLER_<NAME>_VERSION
   * PLUGIN_HANDLER_<NAME>_ADDRESS
   * PLUGIN_HANDLER_<NAME>_PROJECT_IDS
   * ```
   *
   * Unlike most of the other environment key values defined in the [EnvKey]
   * object, these constants define components of wildcard environment keys
   * which may be specified with any arbitrary `<NAME>` value between the
   * defined prefix value and suffix options.
   *
   * The environment variables set using the prefix and suffixes defined below
   * must appear in groups that contain all suffixes.  For example, given the
   * `<NAME>` value `"RNASEQ"` the following environment variables must all be
   * present:
   *
   * ```
   * PLUGIN_HANDLER_RNASEQ_NAME
   * PLUGIN_HANDLER_RNASEQ_VERSION
   * PLUGIN_HANDLER_RNASEQ_ADDRESS
   * PLUGIN_HANDLER_RNASEQ_PROJECT_IDS
   * ```
   *
   * The types of each of the variables is defined in the comment attached to
   * each of the suffix variables in the [Handlers] object below.
   */
  object Handlers {

    const val Prefix = "PLUGIN_HANDLER_"

    /**
     * Represents the environment variable `PLUGIN_HANDLER_{NAME}_NAME` where
     * `{NAME}` is a wildcard matching any string.
     *
     * This environment variable declares the name for a dataset type.
     *
     * Type: String
     * Required: yes
     */
    const val NameSuffix = "_NAME"

    /**
     * Represents the environment variable `PLUGIN_HANDLER_{NAME}_VERSION` where
     * `{NAME}` is a wildcard matching any string.
     *
     * This environment variable declares the version for a dataset type.
     *
     * Type: String
     * Required: yes
     */
    const val VersionSuffix = "_VERSION"

    /**
     * Represents the environment variable `PLUGIN_HANDLER_{NAME}_ADDRESS` where
     * `{NAME}` is a wildcard matching any string.
     *
     * This environment variable declares the host address (hostname and port)
     * of the plugin handler service for datasets of the type named by the
     * wildcard environment variable `PLUGIN_HANDLER_{NAME}_NAME`.
     *
     * Type: HostAddress (`"host:port"`)
     * Required: yes
     */
    const val AddressSuffix = "_ADDRESS"

    /**
     * Represents the environment variable `PLUGIN_HANDLER_{NAME}_PROJECT_IDS`
     * where `{NAME}` is a wildcard matching any string.
     *
     * This environment variable declares the valid projects for a dataset of
     * the type named by the wildcard environment variable
     * `PLUGIN_HANDLER_{NAME}_NAME`.
     *
     * While this environment variable is required, it may be blank.  A blank
     * value means datasets of the defined type are applicable to all projects.
     *
     * Type: List<String> (`"project1,project2,project3"`)
     * Required: yes
     */
    const val ProjectIDsSuffix = "_PROJECT_IDS"

    /**
     * Represents the environment variable `PLUGIN_HANDLER_{NAME}_DISPLAY_NAME`
     * where `{NAME}` is a wildcard matching any string.
     *
     * This environment variable declares the plugin's display name that will be
     * used to show to the end user for a dataset of the type named by the
     * wildcard environment variable `PLUGIN_HANDLER_{NAME}_NAME`.
     *
     * Type: String
     * Required: yes
     */
    const val DisplayNameSuffix = "_DISPLAY_NAME"

    /**
     * Represents the environment variable
     * `PLUGIN_HANDLER_{NAME}_USER_FILES_PATH` where `{NAME}` is a wildcard
     * matching any string.
     *
     * This environment variable declares the plugin's user data files mount
     * path.  User data files will be installed into subdirectories under this
     * mount point.
     *
     * Type: String
     * Required: yes
     */
    const val UserFilesSuffix = "_USER_FILES_PATH"
  }

  object HardDeleteTriggerHandler {

    /**
     * Type: UInt
     * Required: no
     */
    const val WorkerPoolSize = "HARD_DELETE_HANDLER_WORKER_POOL_SIZE"

    /**
     * Type: UInt
     * Required: no
     */
    const val WorkQueueSize = "HARD_DELETE_HANDLER_WORK_QUEUE_SIZE"

    /**
     * Type: String
     * Required: no
     */
    const val KafkaConsumerClientID = "HARD_DELETE_HANDLER_KAFKA_CONSUMER_CLIENT_ID"
  }

  object ImportTriggerHandler {

    /**
     * Type: UInt
     * Required: no
     */
    const val WorkerPoolSize = "IMPORT_HANDLER_WORKER_POOL_SIZE"

    /**
     * Type: UInt
     * Required: no
     */
    const val WorkQueueSize = "IMPORT_HANDLER_WORK_QUEUE_SIZE"

    /**
     * Type: String
     * Required: no
     */
    const val KafkaConsumerClientID = "IMPORT_HANDLER_KAFKA_CONSUMER_CLIENT_ID"
  }

  object InstallDataTriggerHandler {

    /**
     * Type: UInt
     * Required: no
     */
    const val WorkerPoolSize = "INSTALL_DATA_HANDLER_WORKER_POOL_SIZE"

    /**
     * Type: UInt
     * Required: no
     */
    const val WorkQueueSize = "INSTALL_DATA_HANDLER_WORK_QUEUE_SIZE"

    /**
     * Type: String
     * Required: no
     */
    const val KafkaConsumerClientID = "INSTALL_DATA_HANDLER_KAFKA_CONSUMER_CLIENT_ID"
  }

  object Kafka {

    /**
     * Type: List<HostAddress> (`"host:port,host:port"`)
     * Required: yes
     */
    const val Servers = "KAFKA_SERVERS"

    object Consumer {

      /**
       * Type: Duration
       * Required: no
       */
      const val AutoCommitInterval = "KAFKA_CONSUMER_AUTO_COMMIT_INTERVAL"

      /**
       * Type: Enum("earliest"|"latest"|"none")
       * Required: no
       */
      const val AutoOffsetReset = "KAFKA_CONSUMER_AUTO_OFFSET_RESET"

      /**
       * Type: Duration
       * Required: no
       */
      const val ConnectionsMaxIdle = "KAFKA_CONSUMER_CONNECTIONS_MAX_IDLE"

      /**
       * Type: Duration
       * Required: no
       */
      const val DefaultAPITimeout = "KAFKA_CONSUMER_DEFAULT_API_TIMEOUT"

      /**
       * Type: Boolean
       * Required: no
       */
      const val EnableAutoCommit = "KAFKA_CONSUMER_ENABLE_AUTO_COMMIT"

      /**
       * Type: UInt
       * Required: no
       */
      const val FetchMaxBytes = "KAFKA_CONSUMER_FETCH_MAX_BYTES"

      /**
       * Type: UInt
       * Required: no
       */
      const val FetchMinBytes = "KAFKA_CONSUMER_FETCH_MIN_BYTES"

      /**
       * Type: String
       * Required: yes
       */
      const val GroupID = "KAFKA_CONSUMER_GROUP_ID"

      /**
       * Type: String
       * Required: no
       */
      const val GroupInstanceID = "KAFKA_CONSUMER_GROUP_INSTANCE_ID"

      /**
       * Type: Duration
       * Required: no
       */
      const val HeartbeatInterval = "KAFKA_CONSUMER_HEARTBEAT_INTERVAL"

      /**
       * Type: Duration
       * Required: no
       */
      const val MaxPollInterval = "KAFKA_CONSUMER_MAX_POLL_INTERVAL"

      /**
       * Type: UInt
       * Required: no
       */
      const val MaxPollRecords = "KAFKA_CONSUMER_MAX_POLL_RECORDS"

      /**
       * Type: Duration
       * Required: no
       */
      const val PollDuration = "KAFKA_CONSUMER_POLL_DURATION"

      /**
       * Type: UInt
       * Required: no
       */
      const val ReceiveBufferSizeBytes = "KAFKA_CONSUMER_RECEIVE_BUFFER_SIZE_BYTES"

      /**
       * Type: Duration
       * Required: no
       */
      const val ReconnectBackoffMaxTime = "KAFKA_CONSUMER_RECONNECT_BACKOFF_MAX_TIME"

      /**
       * Type: Duration
       * Required: no
       */
      const val ReconnectBackoffTime = "KAFKA_CONSUMER_RECONNECT_BACKOFF_TIME"

      /**
       * Type: Duration
       * Required: no
       */
      const val RequestTimeout = "KAFKA_CONSUMER_REQUEST_TIMEOUT"

      /**
       * Type: Duration
       * Required: no
       */
      const val RetryBackoffTime = "KAFKA_CONSUMER_RETRY_BACKOFF_TIME"

      /**
       * Type: UInt
       * Required: no
       */
      const val SendBufferSizeBytes = "KAFKA_CONSUMER_SEND_BUFFER_SIZE_BYTES"

      /**
       * Type: Duration
       * Required: no
       */
      const val SessionTimeout = "KAFKA_CONSUMER_SESSION_TIMEOUT"
    }

    object Producer {

      /**
       * Type: UInt
       * Required: no
       */
      const val BatchSize = "KAFKA_PRODUCER_BATCH_SIZE"

      /**
       * Type: UInt
       * Required: no
       */
      const val BufferMemoryBytes = "KAFKA_PRODUCER_BUFFER_MEMORY_BYTES"

      /**
       * Type: String
       * Required: yes
       */
      const val ClientID = "KAFKA_PRODUCER_CLIENT_ID"

      /**
       * Type: Enum("none"|"gzip"|"snappy"|"lz4"|"zstd")
       * Required: no
       */
      const val CompressionType = "KAFKA_PRODUCER_COMPRESSION_TYPE"

      /**
       * Type: Duration
       * Required: no
       */
      const val ConnectionsMaxIdle = "KAFKA_PRODUCER_CONNECTIONS_MAX_IDLE"

      /**
       * Type: Duration
       * Required: no
       */
      const val DeliveryTimeout = "KAFKA_PRODUCER_DELIVERY_TIMEOUT"

      /**
       * Type: Duration
       * Required: no
       */
      const val LingerTime = "KAFKA_PRODUCER_LINGER_TIME"

      /**
       * Type: Duration
       * Required: no
       */
      const val MaxBlockingTimeout = "KAFKA_PRODUCER_MAX_BLOCKING_TIMEOUT"

      /**
       * Type: UInt
       * Required: no
       */
      const val MaxRequestSizeBytes = "KAFKA_PRODUCER_MAX_REQUEST_SIZE_BYTES"

      /**
       * Type: Uint
       * Required: no
       */
      const val ReceiveBufferSizeBytes = "KAFKA_PRODUCER_RECEIVE_BUFFER_SIZE_BYTES"

      /**
       * Type: Duration
       * Required: no
       */
      const val ReconnectBackoffMaxTime = "KAFKA_PRODUCER_RECONNECT_BACKOFF_MAX_TIME"

      /**
       * Type: Duration
       * Required: no
       */
      const val ReconnectBackoffTime = "KAFKA_PRODUCER_RECONNECT_BACKOFF_TIME"

      /**
       * Type: Duration
       * Required: no
       */
      const val RequestTimeout = "KAFKA_PRODUCER_REQUEST_TIMEOUT"

      /**
       * Type: Duration
       * Required: no
       */
      const val RetryBackoffTime = "KAFKA_PRODUCER_RETRY_BACKOFF_TIME"

      /**
       * Type: UInt
       * Required: no
       */
      const val SendBufferSizeBytes = "KAFKA_PRODUCER_SEND_BUFFER_SIZE_BYTES"

      /**
       * Type: UInt
       * Required: no
       */
      const val SendRetries = "KAFKA_PRODUCER_SEND_RETRIES"
    }

    object Topic {

      /**
       * Type: String
       * Required: no
       */
      const val HardDeleteTriggers = "KAFKA_TOPIC_HARD_DELETE_TRIGGERS"

      /**
       * Type: String
       * Required: no
       */
      const val ImportTriggers = "KAFKA_TOPIC_IMPORT_TRIGGERS"

      /**
       * Type: String
       * Required: no
       */
      const val InstallTriggers = "KAFKA_TOPIC_INSTALL_TRIGGERS"

      /**
       * Type: String
       * Required: no
       */
      const val ShareTriggers = "KAFKA_TOPIC_SHARE_TRIGGERS"

      /**
       * Type: String
       * Required: no
       */
      const val SoftDeleteTriggers = "KAFKA_TOPIC_SOFT_DELETE_TRIGGERS"

      /**
       * Type: String
       * Required: no
       */
      const val UpdateMetaTriggers = "KAFKA_TOPIC_UPDATE_META_TRIGGERS"

      /**
       * Type: String
       * Required: no
       */
      const val ReconciliationTriggers = "KAFKA_TOPIC_RECONCILIATION_TRIGGERS"
    }

    object MessageKey {
      /**
       * Type: String
       * Required: no
       */
      const val HardDeleteTriggers = "KAFKA_MESSAGE_KEY_HARD_DELETE_TRIGGERS"

      /**
       * Type: String
       * Required: no
       */
      const val ImportTriggers = "KAFKA_MESSAGE_KEY_IMPORT_TRIGGERS"

      /**
       * Type: String
       * Required: no
       */
      const val InstallTriggers = "KAFKA_MESSAGE_KEY_INSTALL_TRIGGERS"

      /**
       * Type: String
       * Required: no
       */
      const val ShareTriggers = "KAFKA_MESSAGE_KEY_SHARE_TRIGGERS"

      /**
       * Type: String
       * Required: no
       */
      const val SoftDeleteTriggers = "KAFKA_MESSAGE_KEY_SOFT_DELETE_TRIGGERS"

      /**
       * Type: String
       * Required: no
       */
      const val UpdateMetaTriggers = "KAFKA_MESSAGE_KEY_UPDATE_META_TRIGGERS"

      /**
       * Type: String
       * Required: no
       */
      const val ReconciliationTriggers = "KAFKA_MESSAGE_KEY_RECONCILIATION_TRIGGERS"
    }
  }

  /**
   * Configuration variables for the Dataset Pruner component of VDI.
   */
  object Pruner {

    /**
     * How old a dataset must be before it is considered "prunable" from S3.
     *
     * Type: Duration
     * Required: no
     */
    const val DeletionThreshold = "DATASET_PRUNING_DELETION_THRESHOLD"

    /**
     * Interval at which the automated pruning job will be run.
     *
     * Type: Duration
     * Required: no
     */
    const val PruningInterval = "DATASET_PRUNING_INTERVAL"

    /**
     * Interval at which the automated pruning module will wake up to check for
     * the service shutdown signal.
     *
     * This value should be fairly short as, assuming a pruning job is not in
     * progress, this defines the wait time for a service shutdown.
     *
     * Type: Duration
     * Required: no
     */
    const val PruningWakeupInterval = "DATASET_PRUNING_WAKEUP_INTERVAL"
  }

  /**
   * VDI file upload quotas and maximums.
   */
  object Quotas {

    /**
     * Maximum file size for a user upload in bytes.
     *
     * Type: ULong
     * Required: no
     */
    const val MaxUploadFileSize = "MAX_UPLOAD_FILE_SIZE"

    /**
     * Quota cap for user uploads in bytes.
     *
     * Type: ULong
     * Required: no
     */
    const val UserUploadQuota = "USER_UPLOAD_QUOTA"
  }

  object Rabbit {
    const val ConnectionName    = "GLOBAL_RABBIT_CONNECTION_NAME"
    const val Host              = "GLOBAL_RABBIT_HOST"
    const val Password          = "GLOBAL_RABBIT_PASSWORD"
    const val PollingInterval   = "GLOBAL_RABBIT_VDI_POLLING_INTERVAL"
    const val Port              = "GLOBAL_RABBIT_PORT"
    const val Username          = "GLOBAL_RABBIT_USERNAME"
    const val UseTLS            = "GLOBAL_RABBIT_USE_TLS"
    const val ConnectionTimeout = "GLOBAL_RABBIT_CONNECTION_TIMEOUT"

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

  object Reconciler {
    /**
     * Whether the full dataset reconciliation daemon should be enabled at all.
     *
     * Type: Boolean
     * Required: no
     */
    const val Enabled = "RECONCILER_FULL_ENABLED"

    /**
     * Run interval for the full reconciliation process.
     *
     * Type: Duration
     * Required: no
     */
    const val FullRunInterval = "RECONCILER_FULL_RUN_INTERVAL"

    /**
     * Run interval for the slim reconciliation process.
     *
     * Type: Duration
     * Required: no
     */
    const val SlimRunInterval = "RECONCILER_SLIM_RUN_INTERVAL"

    /**
     * Whether the reconciler should perform delete operations.  If set to
     * `false`, the reconciler will only log when a delete would have taken
     * place.
     *
     * Type: Boolean
     * Required: no
     */
    const val DeletesEnabled = "RECONCILER_DELETES_ENABLED"
  }

  object ReconciliationTriggerHandler {

    /**
     * Type: UInt
     * Required: no
     */
    const val WorkerPoolSize = "RECONCILIATION_HANDLER_WORKER_POOL_SIZE"

    /**
     * Type: UInt
     * Required: no
     */
    const val WorkQueueSize = "RECONCILIATION_HANDLER_WORK_QUEUE_SIZE"

    /**
     * Type: String
     * Required: no
     */
    const val KafkaConsumerClientID = "RECONCILIATION_HANDLER_KAFKA_CONSUMER_CLIENT_ID"
  }

  /**
   * Connection configuration for VDI to communicate with an S3 compatible service.
   */
  object S3 {

    /**
     * Type: String
     * Required: yes
     */
    const val AccessToken = "S3_ACCESS_TOKEN"

    /**
     * Type: String
     * Required: yes
     */
    const val BucketName  = "S3_BUCKET_NAME"

    /**
     * Type: String
     * Required: yes
     */
    const val Host        = "S3_HOST"

    /**
     * Type: UShort
     * Required: yes
     */
    const val Port        = "S3_PORT"

    /**
     * Type: String
     * Required: yes
     */
    const val SecretKey   = "S3_SECRET_KEY"

    /**
     * Type: Boolean
     * Required: yes
     */
    const val UseHTTPS    = "S3_USE_HTTPS"
  }
  object ShareTriggerHandler {

    /**
     * Type: UInt
     * Required: no
     */
    const val WorkerPoolSize = "SHARE_HANDLER_WORKER_POOL_SIZE"

    /**
     * Type: UInt
     * Required: no
     */
    const val WorkQueueSize = "SHARE_HANDLER_WORK_QUEUE_SIZE"

    /**
     * Type: String
     * Required: no
     */
    const val KafkaConsumerClientID = "SHARE_HANDLER_KAFKA_CONSUMER_CLIENT_ID"
  }

  object SoftDeleteTriggerHandler {

    /**
     * Type: UInt
     * Required: no
     */
    const val WorkerPoolSize = "SOFT_DELETE_HANDLER_WORKER_POOL_SIZE"

    /**
     * Type: UInt
     * Required: no
     */
    const val WorkQueueSize = "SOFT_DELETE_HANDLER_WORK_QUEUE_SIZE"

    /**
     * Type: String
     * Required: no
     */
    const val KafkaConsumerClientID = "SOFT_DELETE_HANDLER_KAFKA_CONSUMER_CLIENT_ID"
  }

  object UpdateMetaTriggerHandler {

    /**
     * Type: UInt
     * Required: no
     */
    const val WorkerPoolSize = "UPDATE_META_HANDLER_WORKER_POOL_SIZE"

    /**
     * Type: UInt
     * Required: no
     */
    const val WorkQueueSize = "UPDATE_META_HANDLER_WORK_QUEUE_SIZE"

    /**
     * Type: String
     * Required: no
     */
    const val KafkaConsumerClientID = "UPDATE_META_HANDLER_KAFKA_CONSUMER_CLIENT_ID"
  }
}
