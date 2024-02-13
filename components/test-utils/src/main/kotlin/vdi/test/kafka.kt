package vdi.test

import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.kafka.*
import org.veupathdb.vdi.lib.kafka.router.KafkaRouter
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterFactory
import kotlin.time.Duration

fun mockEventMessage(
  userID: UserID? = null,
  datasetID: DatasetID? = null,
  eventSource: EventSource? = null,
): EventMessage =
  mock {
    userID?.also { on { this.userID } doReturn it }
    datasetID?.also { on { this.datasetID } doReturn it }
    eventSource?.also { on { this.eventSource } doReturn it }
  }

fun mockKafkaConsumer(
  pollDuration: Duration? = null,
  topic: String? = null,
  onReceive: () -> List<KafkaMessage> = ::noParamList,
  onClose: Runnable = ::runnable,
): KafkaConsumer =
  mock {
    pollDuration?.also { on { this.pollDuration } doReturn it }
    topic?.also { on { this.topic } doReturn it }
    on { receive() } doAnswer { onReceive() }
    on { close() } doAnswer { onClose() }
  }

fun mockKafkaMessage(key: String? = null, value: String? = null): KafkaMessage =
  mock {
    on { this.key } doReturn key
    value?.also { on { this.value } doReturn it }
  }

fun mockKafkaProducer(
  onSendMessage: (String, KafkaMessage, Boolean) -> Unit = ::triConsumer,
  onSendSerial: (String, KafkaSerializable, Boolean) -> Unit = ::triConsumer,
  onClose: Runnable = ::runnable,
): KafkaProducer =
  mock {
    on { send(any(), any() as KafkaMessage, any()) } doAnswer { onSendMessage(it.getArgument(0), it.getArgument(1), it.getArgument(2)) }
    on { send(any(), any() as KafkaSerializable, any()) } doAnswer { onSendSerial(it.getArgument(0), it.getArgument(1), it.getArgument(2)) }
    on { close() } doAnswer { onClose() }
  }

fun mockKafkaRouter(
  onSendImport: (UserID, DatasetID) -> Unit = ::biConsumer,
  onSendInstall: (UserID, DatasetID) -> Unit = ::biConsumer,
  onSendMeta: (UserID, DatasetID) -> Unit = ::biConsumer,
  onSendUninstall: (UserID, DatasetID) -> Unit = ::biConsumer,
  onSendDelete: (UserID, DatasetID) -> Unit = ::biConsumer,
  onSendShare: (UserID, DatasetID) -> Unit = ::biConsumer,
  onSendReconcile: (UserID, DatasetID) -> Unit = ::biConsumer,
  onClose: Runnable = ::runnable,
): KafkaRouter =
  mock {
    on { sendImportTrigger(any(), any()) } doAnswer { onSendImport(it.getArgument(0), it.getArgument(1)) }
    on { sendInstallTrigger(any(), any()) } doAnswer { onSendInstall(it.getArgument(0), it.getArgument(1)) }
    on { sendUpdateMetaTrigger(any(), any()) } doAnswer { onSendMeta(it.getArgument(0), it.getArgument(1)) }
    on { sendSoftDeleteTrigger(any(), any()) } doAnswer { onSendUninstall(it.getArgument(0), it.getArgument(1)) }
    on { sendHardDeleteTrigger(any(), any()) } doAnswer { onSendDelete(it.getArgument(0), it.getArgument(1)) }
    on { sendShareTrigger(any(), any()) } doAnswer { onSendShare(it.getArgument(0), it.getArgument(1)) }
    on { sendReconciliationTrigger(any(), any()) } doAnswer { onSendReconcile(it.getArgument(0), it.getArgument(1)) }
    on { close() } doAnswer { onClose() }
  }

fun mockKafkaRouterFactory(
  onNewKafkaRouter: () -> KafkaRouter = { mockKafkaRouter() }
): KafkaRouterFactory =
  mock {
    on { newKafkaRouter() } doAnswer { onNewKafkaRouter() }
  }