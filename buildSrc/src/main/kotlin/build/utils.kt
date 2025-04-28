package build

import com.networknt.schema.ExecutionContext
import com.networknt.schema.InputFormat
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import java.io.File
import java.net.URI
import java.util.function.Consumer

fun validateServiceConfig(path: File, schemaURI: URI) {
  JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012)
    .getSchema(schemaURI)
    .validate(path.readText(), InputFormat.YAML, Consumer<ExecutionContext> { ctx: ExecutionContext ->
      ctx.executionConfig.formatAssertionsEnabled = true
    })
      .takeUnless { it.isEmpty() }
      ?.also {
        it.forEach { v ->
          System.err.println(v)
        }
        throw RuntimeException("config failed validation")
      }
}
