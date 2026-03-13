package vdi.lane.sharing;

import org.veupathdb.lib.s3.s34k.S3Config;
import vdi.core.config.kafka.KafkaConfig;
import vdi.core.config.vdi.ObjectStoreConfig;
import vdi.core.config.vdi.VDIConfig;
import vdi.core.config.vdi.lanes.ConsumerLaneConfig;
import vdi.core.config.vdi.lanes.LaneConfig;
import vdi.core.kafka.KafkaConsumerConfig;
import vdi.core.kafka.router.RouterDefaults;
import vdi.core.s3.util.S3Util;

import java.util.Optional;

public class ShareLaneConfig {
  private static final int DEFAULT_WORKER_POOL_SIZE = 10;
  private static final int DEFAULT_WORK_QUEUE_SIZE = DEFAULT_WORKER_POOL_SIZE;
  private static final String DEFAULT_CONSUMER_ID = "share-lane-receive";

  private final int workerCount;
  private final int jobQueueSize;
  private final KafkaConsumerConfig kafkaConfig;
  private final S3Config s3Config;
  private final String s3Bucket;
  private final String eventTopic;
  private final String eventMsgKey;

  public ShareLaneConfig(VDIConfig config) {
    this(
      Optional.ofNullable(config.getLanes()).map(LaneConfig::getSharing),
      config.getObjectStore(),
      config.getKafka()
    );
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private ShareLaneConfig(
    Optional<ConsumerLaneConfig> lane,
    ObjectStoreConfig store,
    KafkaConfig kafka
  ) {
    this.workerCount = lane.map(ConsumerLaneConfig::getWorkerCountInt).orElse(DEFAULT_WORKER_POOL_SIZE);
    this.jobQueueSize = lane.map(ConsumerLaneConfig::getInMemoryQueueSizeInt).orElse(DEFAULT_WORKER_POOL_SIZE);
    this.kafkaConfig = new KafkaConsumerConfig(
      lane.map(ConsumerLaneConfig::getKafkaConsumerID).orElse(DEFAULT_CONSUMER_ID),
      kafka
    );
    this.s3Config = S3Util.S3Config(store);
    this.s3Bucket = store.getBucketName();
    this.eventTopic = lane.map(ConsumerLaneConfig::getEventChannel)
      .orElse(RouterDefaults.getShareTriggerTopic());
    this.eventMsgKey = lane.map(ConsumerLaneConfig::getEventKey)
      .orElse(RouterDefaults.getShareTriggerMessageKey());
  }

  public int getWorkerCount() {
    return workerCount;
  }

  public int getJobQueueSize() {
    return jobQueueSize;
  }

  public KafkaConsumerConfig getKafkaConfig() {
    return kafkaConfig;
  }

  public S3Config getS3Config() {
    return s3Config;
  }

  public String getS3Bucket() {
    return s3Bucket;
  }

  public String getEventTopic() {
    return eventTopic;
  }

  public String getEventMsgKey() {
    return eventMsgKey;
  }
}
