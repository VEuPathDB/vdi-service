package vdi.service.rest

import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.config.Options
import org.veupathdb.lib.container.jaxrs.providers.DependencyProvider
import org.veupathdb.lib.container.jaxrs.server.Server
import org.veupathdb.lib.jaxrs.raml.multipart.JaxRSMultipartUpload
import vdi.lib.config.StackConfig
import vdi.service.rest.config.ServiceConfig
import vdi.service.rest.health.DependencySource
import vdi.service.rest.s3.DatasetStore

class RestService(config: StackConfig) : Server() {
  private val log = LoggerFactory.getLogger(javaClass)

  private val options = ServiceConfig(config)

  init {
    DatasetStore.init(config.vdi.objectStore)
    JaxRSMultipartUpload.maxFileUploadSize = options.uploads.maxUploadSize.toLong()
  }

  fun main(args: Array<String>) {
    log.info("starting rest-service module")
    DependencyProvider.getInstance().registerDependencySource(DependencySource())
    start(args)
  }

  override fun newResourceConfig(opts: Options) = Resources(opts as ServiceConfig)

  override fun newOptions() = options
}
