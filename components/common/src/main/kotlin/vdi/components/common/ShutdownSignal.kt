package vdi.components.common

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel

class ShutdownSignal {
  private val channel = Channel<Unit>(1, BufferOverflow.DROP_OLDEST)

  @Volatile
  private var triggered = false

  suspend fun trigger() {
    channel.send(Unit)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  suspend fun isTriggered(): Boolean {
    return if (triggered)
      true
    else if (channel.isEmpty)
      false
    else {
      channel.receive()
      triggered = true
      true
    }
  }
}