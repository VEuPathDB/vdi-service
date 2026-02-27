package vdi.service.rest;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ServerProperties;
import org.veupathdb.lib.container.jaxrs.server.ContainerResources;
import vdi.config.raw.ManifestConfig;
import vdi.core.config.StackConfig;
import vdi.service.rest.config.ServiceConfig;
import vdi.service.rest.config.UploadConfig;
import vdi.service.rest.server.controllers.*;

public class Resources extends ContainerResources {
  public Resources(ServiceConfig opts) {
    super(opts);

    if (opts.getAuthEnabled())
      enableAuth();

    property(ServerProperties.WADL_FEATURE_DISABLE, true);
    property(ServerProperties.BV_FEATURE_DISABLE, true);
    property(ServerProperties.JSON_BINDING_FEATURE_DISABLE, true);
    property(ServerProperties.JSON_PROCESSING_FEATURE_DISABLE, true);
    property(ServerProperties.MOXY_JSON_FEATURE_DISABLE, true);

    if (opts.getEnableTrace())
      enableJerseyTrace();

    register(new AbstractBinder() {
      @Override
      protected void configure() {
        bind(opts.getUploads()).to(UploadConfig.class);
        bind(opts.getManifestConfig()).to(ManifestConfig.class);
        bind(opts.getStackConfig()).to(StackConfig.class);
      }
    });
  }

  @Override
  protected Object[] resources() {
    return new Object[] {
      AdminReports.class,
      AdminRPC.class,
      CommunityDatasets.class,
      DatasetByID.class,
      DatasetFiles.class,
      DatasetListController.class,
      DatasetSharePut.class,
      MetaInfo.class,
      PluginInfo.class,
      UserInfo.class
    };
  }
}
