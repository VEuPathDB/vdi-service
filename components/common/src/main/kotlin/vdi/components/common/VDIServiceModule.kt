package vdi.components.common

interface VDIServiceModule {
  suspend fun start()
  suspend fun stop()
}