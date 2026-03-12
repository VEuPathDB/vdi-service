package vdi.service.rest;

import org.veupathdb.lib.container.jaxrs.config.Options;
import org.veupathdb.lib.container.jaxrs.providers.DependencyProvider;
import org.veupathdb.lib.container.jaxrs.server.ContainerResources;
import org.veupathdb.lib.container.jaxrs.server.Server;
import org.veupathdb.lib.jaxrs.raml.multipart.JaxRSMultipartUpload;
import vdi.config.raw.ManifestConfig;
import vdi.core.config.StackConfig;
import vdi.core.db.app.AppDatabaseRegistry;
import vdi.core.db.app.AppDbManager;
import vdi.core.db.cache.CacheDbManager;
import vdi.core.install.InstallTargetRegistry;
import vdi.core.plugin.registry.PluginRegistry;
import vdi.logging.StackLogging;
import vdi.service.rest.config.ServiceConfig;
import vdi.service.rest.health.DependencySource;
import vdi.service.rest.s3.DatasetStore;

public class RestService extends Server {
  private final ServiceConfig options;

  public RestService(
    StackConfig config,
    ManifestConfig manifest
  ) {
    options = new ServiceConfig(config, manifest);

    eagerLoad(options);
  }

  public void main(String[] args) {
    DependencyProvider.getInstance().registerDependencySource(new DependencySource());
    start(args);
  }

  @Override
  protected ContainerResources newResourceConfig(Options opts) {
    return null;
  }

  @Override
  public ServiceConfig newOptions() {
    return options;
  }

  private static void eagerLoad(ServiceConfig config) {
    StackLogging.getMetaLogger().info("loading rest service dependencies");

    // Imported to perform static field initialization on startup (fail fast)
    @SuppressWarnings("unused")
    var staticInits = new Object[]{
      AppDatabaseRegistry.INSTANCE,
      InstallTargetRegistry.INSTANCE,
      PluginRegistry.INSTANCE
    };

    AppDbManager.getInstance();
    CacheDbManager.getInstance();

    DatasetStore.init(config.getStackConfig().getVdi().getObjectStore());
    JaxRSMultipartUpload.setMaxFileUploadSize(config.getUploads().maxUploadSize());

  }
}
