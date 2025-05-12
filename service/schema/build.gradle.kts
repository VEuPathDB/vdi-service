import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.networknt.schema.ExecutionContext
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import org.yaml.snakeyaml.Yaml
import vdi.ConfigSchemaCompiler

plugins {
  `java-library`
}

val jsonSchemaBuildDir = layout.buildDirectory.dir("json-schema").get().asFile

sourceSets.main {
  resources.srcDir(jsonSchemaBuildDir)
}

tasks.processResources { dependsOn("build-config-schema-resource", "build-dataset-schema-resources") }

tasks.clean { delete(jsonSchemaBuildDir) }

tasks.register("build-config-schema-resource") {
  ConfigSchemaCompiler.init(this, project)

  doLast {
    ConfigSchemaCompiler.run()
  }
}

tasks.register("build-dataset-schema-resources") {
  val outputDir = jsonSchemaBuildDir.resolve("schema/data/")
  val schemaSourceDir = file("data/")

  inputs.dir(schemaSourceDir)
  outputs.dir(outputDir)

  doFirst {
    val json = ObjectMapper()

    val metaSchema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012)
      .getSchema(schemaSourceDir.resolve("dataset-characteristics.metaschema.json").toURI())

    outputDir.mkdirs()

    schemaSourceDir
      .listFiles()!!
      .onEach {
        if (it.name.endsWith("json"))
          it.copyTo(outputDir.resolve(it.name), true)
      }
      .filter { it.name.endsWith("yml") }
      .forEach {
        val content = json.convertValue(Yaml().loadAs(it.readText(), Any::class.java), ObjectNode::class.java)
          .apply { remove("\$schema") }

        metaSchema.validate(content) { ctx: ExecutionContext ->
          ctx.executionConfig.formatAssertionsEnabled = true
        }
          .takeUnless { it.isEmpty() }
          ?.also {
            System.err.println(json.writerWithDefaultPrettyPrinter().writeValueAsString(it))
            throw IllegalStateException("invalid dataset property schema")
          }

        json.writeValue(outputDir.resolve(it.name.substringBeforeLast('.') + ".json"), content)
      }
  }
}
