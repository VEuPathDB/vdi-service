package vdi.lane.imports;

import org.veupathdb.lib.s3.s34k.S3Config;
import vdi.core.config.kafka.KafkaConfig;
import vdi.core.config.vdi.ObjectStoreConfig;
import vdi.core.config.vdi.VDIConfig;
import vdi.core.config.vdi.lanes.ConsumerLaneConfig;
import vdi.core.config.vdi.lanes.LaneConfigBase;
import vdi.core.kafka.KafkaConsumerConfig;
import vdi.core.kafka.router.RouterDefaults;
import vdi.core.s3.util.S3Util;

import java.util.Optional;

public class ImportLaneConfig {
  private static final int DEFAULT_WORKER_POOL_SIZE = 10;
  private static final int DEFAULT_WORKER_QUEUE_SIZE = 10;
  private static final String DEFAULT_CONSUMER_ID = "import-lane-receive";

  private final int workerCount;
  private final int jobQueueSize;
  /**
   * Kafka Consumer Client Configuration.
   */
  private final KafkaConsumerConfig kafkaConfig;
  /**
   * Import Triggers Message Key
   * <p>
   * This value is the expected message key for import trigger messages that
   * will be listened for coming from the import trigger topic.
   * <p>
   * Messages received on the import trigger topic that do not have this message
   * key will be ignored with a warning.
   */
  private final String eventMsgKey;
  /**
   * Import Triggers Topic
   * <p>
   * Name of the Kafka topic that import trigger messages will be listened for
   * on.
   */
  private final String eventChannel;
  private final S3Config s3Config;
  private final String s3Bucket;

  public ImportLaneConfig(VDIConfig config) {
    this(
      config.getLanes() == null ? null : config.getLanes().getImport(),
      config.getObjectStore(),
      config.getKafka()
    );
  }

  public ImportLaneConfig(ConsumerLaneConfig lane, ObjectStoreConfig store, KafkaConfig kafka) {
    Optional<ConsumerLaneConfig> optLane = Optional.ofNullable(lane);

    this.workerCount = optLane.map(LaneConfigBase::getWorkerCountInt).orElse(DEFAULT_WORKER_POOL_SIZE);
    this.jobQueueSize = optLane.map(LaneConfigBase::getInMemoryQueueSizeInt).orElse(DEFAULT_WORKER_QUEUE_SIZE);
    this.kafkaConfig = new KafkaConsumerConfig(
      optLane.map(LaneConfigBase::getKafkaConsumerID).orElse(DEFAULT_CONSUMER_ID),
      kafka
    );
    this.eventMsgKey = optLane.map(LaneConfigBase::getEventKey).orElse(RouterDefaults.getImportTriggerMessageKey());
    this.eventChannel = optLane.map(LaneConfigBase::getEventChannel).orElse(RouterDefaults.getImportTriggerTopic());
    this.s3Config = S3Util.S3Config(store);
    this.s3Bucket = store.getBucketName();
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

  public String getEventMsgKey() {
    return eventMsgKey;
  }

  public String getEventChannel() {
    return eventChannel;
  }

  public S3Config getS3Config() {
    return s3Config;
  }

  public String getS3Bucket() {
    return s3Bucket;
  }
}
