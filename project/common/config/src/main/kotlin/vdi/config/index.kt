@file:JvmName("EntryPoint")
package vdi.config

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.networknt.schema.ExecutionContext
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import vdi.json.JSON
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path
import java.util.jar.Manifest
import kotlin.io.path.Path
import kotlin.io.path.readText
import vdi.config.parse.serde.interpolateFrom
import vdi.config.raw.ManifestConfig
import vdi.config.raw.StackConfig
import vdi.logging.MetaLogger

fun StackConfig(path: Path): StackConfig {
  val validator = StackConfig::class.java.getResourceAsStream("/schema/config/full-config.json")
    .use { JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012).getSchema(it)!! }

  val json = JSON.convertValue<ObjectNode>(Yaml().load<Any>(path.readText().interpolateFrom(System.getenv())))
    .apply { remove(listOf("definitions", "\$schema")) }

  validator.validate(json) { ctx: ExecutionContext -> ctx.executionConfig.formatAssertionsEnabled = true }
    ?.takeUnless { it.isEmpty() }
    ?.also {
      MetaLogger
        .error("service config validation failed:\n{}", JSON.writerWithDefaultPrettyPrinter().writeValueAsString(it))
      throw IllegalStateException("service config validation failed")
    }

  return JSON.convertValue(json)
}

var cachedConfig: StackConfig? = null

fun loadAndCacheStackConfig(path: Path = Path(System.getenv("VDI_CONFIG_PATH") ?: "/etc/vdi/config.yml")) =
  cachedConfig ?: StackConfig(path).also { cachedConfig = it }

fun loadManifestConfig() =
  ManifestConfig::class.java.classLoader.getResourceAsStream("META-INF/MANIFEST.MF")
    .use(::Manifest)
    .let(::ManifestConfig)
