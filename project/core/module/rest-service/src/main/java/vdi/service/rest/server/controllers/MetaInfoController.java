package vdi.service.rest.server.controllers;

import jakarta.ws.rs.core.Context;
import vdi.config.raw.ManifestConfig;
import vdi.config.raw.vdi.InstallTargetConfig;
import vdi.core.config.StackConfig;
import vdi.core.config.vdi.RestServiceConfig;
import vdi.core.config.vdi.daemons.DaemonConfig;
import vdi.service.rest.config.SupportedArchiveType;
import vdi.service.rest.generated.model.*;
import vdi.service.rest.generated.resources.MetaInfo;

public class MetaInfoController implements MetaInfo {
  private final ManifestConfig manifestConfig;
  private final StackConfig stackConfig;

  public MetaInfoController(
    @Context ManifestConfig manifestConfig,
    @Context StackConfig stackConfig
  ) {
    this.manifestConfig = manifestConfig;
    this.stackConfig = stackConfig;
  }

  @Override
  public GetMetaInfoResponse getMetaInfo() {
    var out = new ServiceMetadataResponseBodyImpl();
    out.setBuildInfo(initBuildInfo(manifestConfig));
    out.setConfiguration(initConfigDetails(stackConfig));
    out.setFeatures(initServiceFeatures());
    return GetMetaInfoResponse.respond200WithApplicationJson(out);
  }

  private static ServiceMetadataBuildInfoOutput initBuildInfo(ManifestConfig metadata) {
    var out = new ServiceMetadataBuildInfoOutputImpl();

    out.setGitTag(metadata.getGitTag());
    out.setGitCommit(metadata.getGitCommit());
    out.setGitBranch(metadata.getGitBranch());
    out.setGitUrl(metadata.getGitURL());
    out.setBuildId(metadata.getBuildID());
    out.setBuildNumber(metadata.getBuildNumber());
    out.setBuildTime(metadata.getBuildTime());

    return out;
  }

  private static ServiceConfigurationDetails initConfigDetails(StackConfig stack) {
    var out = new ServiceConfigurationDetailsImpl();

    out.setApi(initApiConfig(stack.getVdi().getRestService()));
    out.setDaemons(initDaemonConfig(stack.getVdi().getDaemons()));
    out.setInstallTargets(stack.getVdi()
      .getInstallTargets()
      .stream()
      .map(MetaInfoController::initInstallTarget)
      .toList());

    return out;
  }

  private static APIServiceConfiguration initApiConfig(RestServiceConfig service) {
    var out = new APIServiceConfigurationImpl();

    out.setMaxUploadSize(service.getLongMaxUploadSize());
    out.setUserMaxStorageSize(service.getLongUserMaxStorageSize());

    return out;
  }

  private static DaemonConfiguration initDaemonConfig(DaemonConfig daemon) {
    var rec = new ReconcilerConfigImpl();
    rec.setEnabled(daemon.getReconciler().getEnabled());
    rec.setFullRunInterval(daemon.getReconciler().getJavaFullRunInterval().toString());
    rec.setSlimRunInterval(daemon.getReconciler().getJavaSlimRunInterval().toString());
    rec.setPerformDeletes(daemon.getReconciler().getPerformDeletes());

    var out = new DaemonConfigurationImpl();
    out.setReconciler(rec);

    return out;
  }

  private static InstallTarget initInstallTarget(InstallTargetConfig config) {
    var out = new InstallTargetImpl();

    out.setId(config.getTargetID());
    out.setName(config.getTargetName());

    return out;
  }

  private static ServiceFeatures initServiceFeatures() {
    var out = new ServiceFeaturesImpl();
    out.setSupportedArchiveTypes(SupportedArchiveType.getAllSupportedExtensions());
    return out;
  }
}
