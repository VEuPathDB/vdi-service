package vdi.service.rest.server.outputs

import vdi.core.config.ManifestConfig
import vdi.service.rest.generated.model.ServiceMetadataBuildInfoOutputImpl
import vdi.service.rest.generated.model.ServiceMetadataResponseBody
import vdi.service.rest.generated.model.ServiceMetadataResponseBodyImpl

fun ServiceMetadataResponseBody(metadata: ManifestConfig): ServiceMetadataResponseBody =
  ServiceMetadataResponseBodyImpl().apply {
    buildInfo = ServiceMetadataBuildInfoOutputImpl().apply {
      gitTag = metadata.gitTag
      gitCommit = metadata.gitCommit
      gitBranch = metadata.gitBranch
      gitUrl = metadata.gitURL
      buildId = metadata.buildID
      buildNumber = metadata.buildNumber
      buildTime = metadata.buildTime
    }
  }
