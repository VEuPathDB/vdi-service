package vdi.service.rest.server.outputs

import vdi.config.raw.ManifestConfig
import vdi.core.config.StackConfig
import vdi.core.config.vdi.daemons.ReconcilerConfig
import vdi.service.rest.generated.model.APIServiceConfigurationImpl
import vdi.service.rest.generated.model.DaemonConfigurationImpl
import vdi.service.rest.generated.model.ReconcilerConfig as APIReconcilerConfig
import vdi.service.rest.generated.model.ReconcilerConfigImpl
import vdi.service.rest.generated.model.ServiceConfigurationDetailsImpl
import vdi.service.rest.generated.model.ServiceFeaturesImpl
import vdi.service.rest.generated.model.ServiceMetadataBuildInfoOutputImpl
import vdi.service.rest.generated.model.ServiceMetadataResponseBody
import vdi.service.rest.generated.model.ServiceMetadataResponseBodyImpl
import vdi.service.rest.util.SupportedArchiveType

fun ServiceMetadataResponseBody(metadata: ManifestConfig, stack: StackConfig): ServiceMetadataResponseBody =
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
    configuration = ServiceConfigurationDetailsImpl().apply {
      daemons = DaemonConfigurationImpl().apply {
        api = APIServiceConfigurationImpl().apply {
          maxUploadSize = stack.vdi.restService.maxUploadSize.toLong()
          userMaxStorageSize = stack.vdi.restService.userMaxStorageSize.toLong()
        }

        reconciler = ReconcilerConfig(stack.vdi.daemons.reconciler)
      }
    }
    features = ServiceFeaturesImpl().apply {
      supportedArchiveTypes = SupportedArchiveType.SupportedExtensions.asList()
    }
  }

private fun ReconcilerConfig(config: ReconcilerConfig): APIReconcilerConfig =
  ReconcilerConfigImpl().apply {
    enabled = config.enabled
    fullRunInterval = config.fullRunInterval.toString()
    slimRunInterval = config.slimRunInterval.toString()
    performDeletes = config.performDeletes
  }