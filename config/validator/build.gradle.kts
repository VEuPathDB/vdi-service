import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.networknt.schema.ExecutionContext
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.net.URI
import java.util.function.Consumer

repositories {
  mavenCentral()
}

tasks.register("validate-config") {
  val path = File(project.properties["config-file"] as String)
  val schemaURI = URI.create("https://veupathdb.github.io/vdi-service/schema/config.json")

  doFirst {
    val jackson = YAMLMapper.builder().build()

    JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012)
      .getSchema(schemaURI)
      .validate(jackson.convertValue(Yaml().load(path.readText()), JsonNode::class.java), Consumer<ExecutionContext> { ctx: ExecutionContext ->
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
}
