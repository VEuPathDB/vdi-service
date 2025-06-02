package vdi.test

import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import kotlin.time.Duration
import vdi.core.kafka.*
import vdi.core.kafka.router.KafkaRouter
import vdi.core.kafka.router.KafkaRouterFactory

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
  topic: MessageTopic? = null,
  onReceive: () -> List<KafkaMessage> = ::noParamList,
  onClose: Runnable = ::runnable,
): KafkaConsumer =
  mock {
    pollDuration?.also { on { this.pollDuration } doReturn it }
    topic?.also { on { this.topic } doReturn it }
    on { receive() } doAnswer { onReceive() }
    on { close() } doAnswer { onClose() }
  }

fun mockKafkaMessage(key: MessageKey? = null, value: String? = null): KafkaMessage =
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
  onSendImport: (UserID, DatasetID, EventSource) -> Unit = ::triConsumer,
  onSendInstall: (UserID, DatasetID, EventSource) -> Unit = ::triConsumer,
  onSendMeta: (UserID, DatasetID, EventSource) -> Unit = ::triConsumer,
  onSendUninstall: (UserID, DatasetID, EventSource) -> Unit = ::triConsumer,
  onSendDelete: (UserID, DatasetID, EventSource) -> Unit = ::triConsumer,
  onSendShare: (UserID, DatasetID, EventSource) -> Unit = ::triConsumer,
  onSendReconcile: (UserID, DatasetID, EventSource) -> Unit = ::triConsumer,
  onClose: Runnable = ::runnable,
): KafkaRouter =
  mock {
    on { sendImportTrigger(any(), any(), any()) } doAnswer { onSendImport(it.getArgument(0), it.getArgument(1), it.getArgument(2)) }
    on { sendInstallTrigger(any(), any(), any()) } doAnswer { onSendInstall(it.getArgument(0), it.getArgument(1), it.getArgument(2)) }
    on { sendUpdateMetaTrigger(any(), any(), any()) } doAnswer { onSendMeta(it.getArgument(0), it.getArgument(1), it.getArgument(2)) }
    on { sendSoftDeleteTrigger(any(), any(), any()) } doAnswer { onSendUninstall(it.getArgument(0), it.getArgument(1), it.getArgument(2)) }
    on { sendHardDeleteTrigger(any(), any(), any()) } doAnswer { onSendDelete(it.getArgument(0), it.getArgument(1), it.getArgument(2)) }
    on { sendShareTrigger(any(), any(), any()) } doAnswer { onSendShare(it.getArgument(0), it.getArgument(1), it.getArgument(2)) }
    on { sendReconciliationTrigger(any(), any(), any()) } doAnswer { onSendReconcile(it.getArgument(0), it.getArgument(1), it.getArgument(2)) }
    on { close() } doAnswer { onClose() }
  }

fun mockKafkaRouterFactory(
  onNewKafkaRouter: () -> KafkaRouter = { mockKafkaRouter() }
): KafkaRouterFactory =
  mock {
    on { newKafkaRouter() } doAnswer { onNewKafkaRouter() }
  }
