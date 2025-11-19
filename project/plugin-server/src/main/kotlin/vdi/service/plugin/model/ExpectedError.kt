package vdi.service.plugin.model

import vdi.io.plugin.responses.PluginResponseStatus

interface ExpectedError {
  val status: PluginResponseStatus

  fun toResponse(): Any
}