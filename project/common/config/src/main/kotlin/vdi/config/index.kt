@file:JvmName("EntryPoint")
package vdi.config

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.networknt.schema.ExecutionContext
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import vdi.json.JSON
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.readText
import vdi.config.parse.ConfigurationException
import vdi.config.parse.serde.interpolateFrom
import vdi.logging.MetaLogger
import vdi.model.field.SecretString

const val DefaultConfigPath = "/etc/vdi/config.yml"

const val DefaultFullStackSchemaPath = "/schema/config/full-config.json"

fun makeDefaultConfigPath() = Path(System.getenv("VDI_CONFIG_PATH") ?: DefaultConfigPath)

inline fun <reified T: Any> loadAndCastConfig(path: Path = makeDefaultConfigPath(), schema: Path): T =
  try {
    JSON.convertValue(loadAndValidateConfig(path, schema))
  } catch (e: IllegalArgumentException) {
    when (val ex = e.cause) {
      is MismatchedInputException -> if (ex.targetType == SecretString::class.java) {
        throw ConfigurationException(ex.message!!.replace(Regex("\\('.+'\\)"), "('***')"))
      }
    }
    throw ConfigurationException(e)
  }

fun loadAndValidateConfig(path: Path, schema: Path): ObjectNode {
  val validator = object {}.javaClass.getResourceAsStream(schema.toString())
    .use { JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012).getSchema(it)!! }

  val json = JSON.convertValue<ObjectNode>(Yaml().load<Any>(path.readText().interpolateFrom(System.getenv())))
    .apply { remove(listOf("definitions", "\$schema")) }

  validator.validate(json) { ctx: ExecutionContext -> ctx.executionConfig.formatAssertionsEnabled = true }
    ?.takeUnless { it.isEmpty() }
    ?.also {
      MetaLogger
        .error("service config validation failed:\n{}", JSON.writerWithDefaultPrettyPrinter().writeValueAsString(it))
      throw ConfigurationException("service config validation failed")
    }

  return json
}
