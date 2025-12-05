package vdi.io.plugin.responses

import vdi.config.raw.ManifestConfig

data class ServerInfoResponse(
  val manifest: ManifestConfig
)
