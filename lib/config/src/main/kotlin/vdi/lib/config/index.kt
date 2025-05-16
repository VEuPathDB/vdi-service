@file:JvmName("EntryPoint")
package vdi.lib.config

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.networknt.schema.ExecutionContext
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.json.JSON
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path
import java.util.jar.Manifest
import kotlin.io.path.Path
import kotlin.io.path.readText
import org.veupathdb.vdi.lib.config.serde.interpolateFrom

fun StackConfig(path: Path): StackConfig {
  val validator = StackConfig::class.java.getResourceAsStream("/schema/config/full-config.json")
    .use { JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012).getSchema(it)!! }

  val json = JSON.convertValue<ObjectNode>(Yaml().load<Any>(path.readText().interpolateFrom(System.getenv())))
    .apply { remove(listOf("definitions", "\$schema")) }

  validator.validate(json) { ctx: ExecutionContext -> ctx.executionConfig.formatAssertionsEnabled = true }
    ?.takeUnless { it.isEmpty() }
    ?.also {
      LoggerFactory.getLogger("ConfigEntrypoint")
        .error("service config validation failed:\n{}", JSON.writerWithDefaultPrettyPrinter().writeValueAsString(it))
      throw IllegalStateException("service config validation failed")
    }

  return JSON.convertValue(json)
}

var cachedConfig: StackConfig? = null

fun loadAndCacheStackConfig(path: Path = Path("/etc/vdi/config.yml")): StackConfig {
  return cachedConfig ?: StackConfig(path).also { cachedConfig = it }
}

fun loadManifestConfig() =
  ManifestConfig::class.java.classLoader.getResourceAsStream("META-INF/MANIFEST.MF")
    .use(::Manifest)
    .let(::ManifestConfig)
