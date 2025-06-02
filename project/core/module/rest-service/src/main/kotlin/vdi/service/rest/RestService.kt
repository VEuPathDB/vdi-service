package vdi.service.rest

import org.veupathdb.lib.container.jaxrs.config.Options
import org.veupathdb.lib.container.jaxrs.providers.DependencyProvider
import org.veupathdb.lib.container.jaxrs.server.Server
import org.veupathdb.lib.jaxrs.raml.multipart.JaxRSMultipartUpload
import vdi.config.raw.ManifestConfig
import vdi.config.raw.StackConfig
import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDatabaseRegistry
import vdi.core.db.cache.CacheDB
import vdi.core.install.InstallTargetRegistry
import vdi.logging.MetaLogger
import vdi.core.plugin.registry.PluginRegistry
import vdi.service.rest.config.ServiceConfig
import vdi.service.rest.health.DependencySource
import vdi.service.rest.s3.DatasetStore

class RestService(config: StackConfig, manifest: ManifestConfig) : Server() {
  private val options = ServiceConfig(config, manifest)

  init {
    // Eager load classes for fail-fast
    MetaLogger.info("eagerly loading rest service dependencies")
    AppDatabaseRegistry
    InstallTargetRegistry
    PluginRegistry
    AppDB()
    CacheDB()

    DatasetStore.init(config.vdi.objectStore)
    JaxRSMultipartUpload.maxFileUploadSize = options.uploads.maxUploadSize.toLong()
  }

  fun main(args: Array<String>) {
    DependencyProvider.getInstance().registerDependencySource(DependencySource())
    start(args)
  }

  override fun newResourceConfig(opts: Options) = Resources(opts as ServiceConfig)

  override fun newOptions() = options
}

