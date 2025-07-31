package vdi.service.rest

import com.fasterxml.jackson.databind.ObjectMapper
import org.glassfish.jersey.internal.inject.AbstractBinder
import org.glassfish.jersey.media.multipart.MultiPartFeature
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.veupathdb.lib.container.jaxrs.server.ContainerResources
import org.veupathdb.lib.container.jaxrs.server.middleware.ErrorMapper
import org.veupathdb.lib.jaxrs.raml.multipart.MultipartApplicationEventListener
import org.veupathdb.lib.jaxrs.raml.multipart.MultipartMessageBodyReader
import vdi.config.raw.ManifestConfig
import vdi.json.JSON
import vdi.service.rest.config.ServerContext
import vdi.service.rest.config.ServiceConfig
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.server.controllers.TestController
import vdi.service.rest.server.middleware.serde.JsonBodyReader
import vdi.service.rest.server.middleware.serde.JsonBodyWriter

class Resources(opts: ServiceConfig) : ContainerResources(opts) {
  val jsonValidator = JsonValidator(opts.jsonSchemaPath, JSON) {}
  val serverContext = ServerContext(
    opts.jsonSchemaPath,
    opts.generatedSourcePackage,
    JSON,
    jsonValidator
  )

  init {
    enableAuth()

    property(ServerProperties.PROVIDER_SCANNING_RECURSIVE, false)
    property(ServerProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true)

    property(ServerProperties.WADL_FEATURE_DISABLE, true)

    property(ServerProperties.BV_FEATURE_DISABLE, true)

    property(ServerProperties.JSON_BINDING_FEATURE_DISABLE, true)
    property(ServerProperties.JSON_PROCESSING_FEATURE_DISABLE, true)
    property(ServerProperties.MOXY_JSON_FEATURE_DISABLE, true)

    property(ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true)

    if (opts.enableTrace)
      enableJerseyTrace()

    super.register(MultiPartFeature::class.java)
    super.register(MultipartApplicationEventListener::class.java)
    super.register(MultipartMessageBodyReader::class.java)
    super.register(ErrorMapper::class.java)
    super.register(JsonBodyReader::class.java)
    super.register(JsonBodyWriter::class.java)
    super.register(TestController::class.java)

    super.register(object: AbstractBinder() {
      override fun configure() {
        bind(JSON).to(ObjectMapper::class.java)
        bind(jsonValidator).to(JsonValidator::class.java)
        bind(serverContext).to(ServerContext::class.java)
        bind(opts.uploads).to(UploadConfig::class.java)
        bind(opts.manifestConfig).to(ManifestConfig::class.java)
      }
    })
  }

  override fun register(componentClass: Class<*>): ResourceConfig {
    if (!componentClass.packageName.contains("org.veupathdb.lib.container."))
      super.register(componentClass)
    return this
  }

  override fun resources() = arrayOf<Any>(
//    AdminReports::class.java,
//    AdminRPC::class.java,
//    CommunityDatasets::class.java,
//    DatasetByID::class.java,
//    DatasetFiles::class.java,
//    DatasetList::class.java,
//    DatasetSharePut::class.java,
//    MetaInfo::class.java,
//    PluginInfo::class.java,
//    UserInfo::class.java,
  )
}
