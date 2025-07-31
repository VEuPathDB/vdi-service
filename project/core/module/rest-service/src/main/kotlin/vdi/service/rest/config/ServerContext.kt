package vdi.service.rest.config

import com.fasterxml.jackson.databind.ObjectMapper
import vdi.service.rest.JsonValidator

data class ServerContext(
  val jsonSchemaPath: String,
  val generatedSourcePackage: String,
  val jsonMapper: ObjectMapper,
  val jsonValidator: JsonValidator,
)