package org.veupathdb.vdi.lib.handler.client

interface PluginHandlerClientResponse {
  val isSuccessResponse: Boolean
  val responseCode: Int
}