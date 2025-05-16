package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import vdi.lib.config.ManifestConfig
import vdi.service.rest.generated.resources.MetaInfo
import vdi.service.rest.server.outputs.ServiceMetadataResponseBody

class MetaInfo(@Context val manifestConfig: ManifestConfig): MetaInfo {
  override fun getMetaInfo(): MetaInfo.GetMetaInfoResponse =
    MetaInfo.GetMetaInfoResponse.respond200WithApplicationJson(ServiceMetadataResponseBody(manifestConfig))
}
