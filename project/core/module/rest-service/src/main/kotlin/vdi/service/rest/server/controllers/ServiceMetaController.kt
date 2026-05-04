package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import vdi.config.raw.ManifestConfig
import vdi.core.config.StackConfig
import vdi.service.rest.config.SupportedArchiveType
import vdi.service.rest.generated.model.APIServiceConfigurationImpl
import vdi.service.rest.generated.model.DaemonConfigurationImpl
import vdi.service.rest.generated.model.InstallTargetImpl
import vdi.service.rest.generated.model.ReconcilerConfigImpl
import vdi.service.rest.generated.model.ServiceConfigurationDetailsImpl
import vdi.service.rest.generated.model.ServiceFeaturesImpl
import vdi.service.rest.generated.model.ServiceMetadataBuildInfoOutputImpl
import vdi.service.rest.generated.model.ServiceMetadataResponseBodyImpl
import vdi.service.rest.generated.resources.ServiceMeta

internal class ServiceMetaController(
  @param:Context private val manifestConfig: ManifestConfig,
  @param:Context private val stackConfig: StackConfig,
): ServiceMeta {
  override fun getServiceMeta(): ServiceMeta.GetServiceMetaResponse {
    return ServiceMetadataResponseBodyImpl()
      .also { out ->
        out.buildInfo = ServiceMetadataBuildInfoOutputImpl().apply {
          gitTag = manifestConfig.gitTag
          gitCommit = manifestConfig.gitCommit
          gitBranch = manifestConfig.gitBranch
          gitUrl = manifestConfig.gitURL
          buildId = manifestConfig.buildID
          buildNumber = manifestConfig.buildNumber
          buildTime = manifestConfig.buildTime
        }

        out.configuration = ServiceConfigurationDetailsImpl().apply {
          api = APIServiceConfigurationImpl().apply {
            val svcConf = stackConfig.vdi.restService

            maxUploadSize = svcConf.longMaxUploadSize
            userMaxStorageSize = svcConf.longUserMaxStorageSize
          }

          daemons = DaemonConfigurationImpl().apply {
            reconciler = ReconcilerConfigImpl().apply {
              val reConf = stackConfig.vdi.daemons.reconciler

              enabled = reConf.enabled
              fullRunInterval = reConf.fullRunInterval.toString()
              slimRunInterval = reConf.slimRunInterval.toString()
              performDeletes = reConf.performDeletes
            }
          }

          installTargets = stackConfig.vdi.installTargets.map {
            InstallTargetImpl().apply {
              id = it.targetID
              name = it.targetName
            }
          }
        }

        out.features = ServiceFeaturesImpl().apply {
          supportedArchiveTypes = SupportedArchiveType.getAllSupportedExtensions().asList()
        }
      }
      .let(ServiceMeta.GetServiceMetaResponse::respond200WithApplicationJson)
  }
}
