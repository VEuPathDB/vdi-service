package vdi.test

import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.core.kafka.*
import vdi.core.kafka.router.KafkaRouter

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

