package vdi.component.async

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

object CachedDispatcher : CoroutineDispatcher() {
  private val executor = Executors.newCachedThreadPool()

  override fun dispatch(context: CoroutineContext, block: Runnable) {
    executor.submit(block)
  }
}