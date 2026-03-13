package vdi.lane.reconciliation;

import org.veupathdb.lib.s3.s34k.S3Config;
import vdi.core.config.kafka.KafkaConfig;
import vdi.core.config.vdi.ObjectStoreConfig;
import vdi.core.config.vdi.VDIConfig;
import vdi.core.config.vdi.lanes.LaneConfig;
import vdi.core.config.vdi.lanes.ProducerLaneConfig;
import vdi.core.kafka.KafkaConsumerConfig;
import vdi.core.kafka.router.KafkaRouterConfig;
import vdi.core.kafka.router.RouterDefaults;
import vdi.core.s3.util.S3Util;

import java.util.Optional;

public class ReconciliationLaneConfig {
  private static final String DEFAULT_CONSUMER_ID = "reconciliation-lane-receive";
  private static final String DEFAULT_PRODUCER_ID = "reconciliation-lane-send";
  private static final int DEFAULT_WORKER_POOL_SIZE = 10;
  private static final int DEFAULT_JOB_QUEUE_SIZE = DEFAULT_WORKER_POOL_SIZE;

  private final int workerCount;
  private final int jobQueueSize;
  private final KafkaConsumerConfig kafkaInConfig;
  private final KafkaRouterConfig kafkaOutConfig;
  private final String eventChannel;
  private final String eventMsgKey;
  private final S3Config s3Config;
  private final String s3Bucket;

  public ReconciliationLaneConfig(VDIConfig config) {
    this(config.getLanes(), config.getObjectStore(), config.getKafka());
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public ReconciliationLaneConfig(
    LaneConfig lanes,
    ObjectStoreConfig store,
    KafkaConfig kafka
  ) {
    Optional<LaneConfig> optLanes = Optional.ofNullable(lanes);
    Optional<ProducerLaneConfig> optRecon = optLanes.map(LaneConfig::getReconciliation);

    this.workerCount = optRecon.map(ProducerLaneConfig::getWorkerCountInt).orElse(DEFAULT_WORKER_POOL_SIZE);
    this.jobQueueSize = optRecon.map(ProducerLaneConfig::getInMemoryQueueSizeInt).orElse(DEFAULT_JOB_QUEUE_SIZE);
    this.kafkaInConfig = new KafkaConsumerConfig(
      optRecon.map(ProducerLaneConfig::getKafkaConsumerID).orElse(DEFAULT_CONSUMER_ID),
      kafka
    );
    this.kafkaOutConfig = new KafkaRouterConfig(
      optRecon.map(ProducerLaneConfig::getKafkaProducerID).orElse(DEFAULT_PRODUCER_ID),
      kafka,
      optLanes.orElse(null)
    );
    this.eventChannel = optRecon.map(ProducerLaneConfig::getEventChannel)
      .orElseGet(RouterDefaults::getReconciliationTriggerTopic);
    this.eventMsgKey = optRecon.map(ProducerLaneConfig::getEventKey)
      .orElseGet(RouterDefaults::getReconciliationTriggerMessageKey);
    this.s3Config = S3Util.S3Config(store);
    this.s3Bucket = store.getBucketName();
  }

  public int getWorkerCount() {
    return workerCount;
  }

  public int getJobQueueSize() {
    return jobQueueSize;
  }

  public KafkaConsumerConfig getKafkaInConfig() {
    return kafkaInConfig;
  }

  public KafkaRouterConfig getKafkaOutConfig() {
    return kafkaOutConfig;
  }

  public String getEventChannel() {
    return eventChannel;
  }

  public String getEventMsgKey() {
    return eventMsgKey;
  }

  public S3Config getS3Config() {
    return s3Config;
  }

  public String getS3Bucket() {
    return s3Bucket;
  }
}
