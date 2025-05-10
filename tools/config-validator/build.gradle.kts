import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.networknt.schema.ExecutionContext
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import org.yaml.snakeyaml.Yaml
import java.io.File

repositories {
  mavenCentral()
}

tasks.register("validate-config") {
  val configPath = File(project.properties["config-file"] as String)
  val schemaPath = File(project.properties["schema-file"] as String)

  doFirst {
    val jackson = YAMLMapper.builder().build()

    JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012)
      .getSchema(schemaPath.toURI())
      .validate(jackson.convertValue(Yaml().load(configPath.readText()), ObjectNode::class.java)) { ctx: ExecutionContext ->
        ctx.executionConfig.formatAssertionsEnabled = true
      }
      .takeUnless { it.isEmpty() }
      ?.also {
        it.forEach { v ->
          System.err.println(v)
        }
        throw RuntimeException("config failed validation")
      }
  }
}
