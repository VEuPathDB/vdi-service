package vdi.lane.soft_delete;

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

public class SoftDeleteLaneConfig {
  private static final int DEFAULT_WORKER_POOL_SIZE = 5;
  private static final int DEFAULT_WORK_QUEUE_SIZE = 5;
  private static final String DEFAULT_CONSUMER_ID = "soft-delete-lane-receive";

  private final int workerCount;
  private final int jobQueueSize;
  private final KafkaConsumerConfig kafkaConfig;
  private final S3Config s3Config;
  private final String s3Bucket;
  private final String eventChannel;
  private final String eventKey;

  public SoftDeleteLaneConfig(VDIConfig config) {
    this(
      Optional.ofNullable(config.getLanes()).map(LaneConfig::getSoftDelete).orElse(null),
      config.getObjectStore(),
      config.getKafka()
    );
  }

  public SoftDeleteLaneConfig(
    ConsumerLaneConfig lane,
    ObjectStoreConfig store,
    KafkaConfig kafka
  ) {
    Optional<ConsumerLaneConfig> optLane = Optional.ofNullable(lane);

    this.workerCount = optLane.map(ConsumerLaneConfig::getWorkerCountInt).orElse(DEFAULT_WORKER_POOL_SIZE);
    this.jobQueueSize = optLane.map(ConsumerLaneConfig::getInMemoryQueueSizeInt).orElse(DEFAULT_WORK_QUEUE_SIZE);
    this.kafkaConfig = new KafkaConsumerConfig(
      optLane.map(ConsumerLaneConfig::getKafkaConsumerID).orElse(DEFAULT_CONSUMER_ID),
      kafka
    );
    this.s3Config = S3Util.S3Config(store);
    this.s3Bucket = store.getBucketName();
    this.eventChannel = optLane.map(ConsumerLaneConfig::getEventChannel)
      .orElse(RouterDefaults.getSoftDeleteTriggerTopic());
    this.eventKey = optLane.map(ConsumerLaneConfig::getEventKey)
      .orElse(RouterDefaults.getSoftDeleteTriggerMessageKey());
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

  public String getEventChannel() {
    return eventChannel;
  }

  public String getEventKey() {
    return eventKey;
  }
}
