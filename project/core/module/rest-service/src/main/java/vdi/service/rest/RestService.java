package vdi.service.rest;

import org.veupathdb.lib.container.jaxrs.config.Options;
import org.veupathdb.lib.container.jaxrs.providers.DependencyProvider;
import org.veupathdb.lib.container.jaxrs.server.ContainerResources;
import org.veupathdb.lib.container.jaxrs.server.Server;
import org.veupathdb.lib.jaxrs.raml.multipart.JaxRSMultipartUpload;
import vdi.config.raw.ManifestConfig;
import vdi.core.config.StackConfig;
import vdi.core.db.app.AppDatabaseRegistry;
import vdi.core.db.app.AppDbProvider;
import vdi.core.db.cache.CacheDbProvider;
import vdi.core.install.InstallTargetRegistry;
import vdi.core.plugin.registry.PluginRegistry;
import vdi.logging.LoggingUtils;
import vdi.service.rest.config.ServiceConfig;
import vdi.service.rest.health.DependencySource;
import vdi.service.rest.s3.DatasetStore;


public class RestService extends Server {
  private final ServiceConfig options;

  public RestService(StackConfig config, ManifestConfig manifest) {
    this.options = new ServiceConfig(config, manifest);
    initFacades(config);
  }

  public void main(String[] args) {
    DependencyProvider.getInstance().registerDependencySource(new DependencySource());
    start(args);
  }

  @Override
  protected ContainerResources newResourceConfig(Options opts) {
    return new Resources(options);
  }

  @Override
  protected Options newOptions() {
    return options;
  }

  private static void initFacades(StackConfig config) {
    LoggingUtils.getMetaLogger().info("eagerly loading rest service dependencies");

    // Classes are only referenced to cause static initialization.
    @SuppressWarnings("unused")
    var foo = new Object[]{
      AppDatabaseRegistry.INSTANCE,
      InstallTargetRegistry.INSTANCE,
      PluginRegistry.INSTANCE
    };

    AppDbProvider.appDb();
    CacheDbProvider.cacheDb();

    DatasetStore.INSTANCE.init(config.getVdi().getObjectStore());
    JaxRSMultipartUpload.setMaxFileUploadSize(config.getVdi().getRestService().getLongMaxUploadSize());
  }
}
