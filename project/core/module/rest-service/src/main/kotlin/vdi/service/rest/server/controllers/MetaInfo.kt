package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import vdi.config.raw.ManifestConfig
import vdi.core.config.StackConfig
import vdi.service.rest.generated.resources.MetaInfo
import vdi.service.rest.server.outputs.ServiceMetadataResponseBody

class MetaInfo(
  @param:Context val manifestConfig: ManifestConfig,
  @param:Context val stackConfig: StackConfig,
): MetaInfo {
  override fun getMetaInfo(): MetaInfo.GetMetaInfoResponse =
    MetaInfo.GetMetaInfoResponse.respond200WithApplicationJson(ServiceMetadataResponseBody(
      manifestConfig,
      stackConfig,
    ))
}
