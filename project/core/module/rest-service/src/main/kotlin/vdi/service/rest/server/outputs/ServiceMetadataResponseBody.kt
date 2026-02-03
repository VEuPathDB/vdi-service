package vdi.service.rest.server.outputs

import vdi.config.raw.ManifestConfig
import vdi.core.config.StackConfig
import vdi.core.config.vdi.daemons.ReconcilerConfig
import vdi.service.rest.generated.model.*
import vdi.service.rest.util.SupportedArchiveType
import vdi.service.rest.generated.model.ReconcilerConfig as APIReconcilerConfig

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
      api = APIServiceConfigurationImpl().apply {
        maxUploadSize = stack.vdi.restService.maxUploadSize.toLong()
        userMaxStorageSize = stack.vdi.restService.userMaxStorageSize.toLong()
      }

      daemons = DaemonConfigurationImpl().apply {
        reconciler = ReconcilerConfig(stack.vdi.daemons.reconciler)
      }

      installTargets = stack.vdi.installTargets.map { tgt ->
        InstallTargetImpl().also {
          it.id = tgt.targetID
          it.name = tgt.targetName
        }
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