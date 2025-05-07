package vdi.lib.config

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.networknt.schema.*
import org.veupathdb.vdi.lib.json.JSON
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.readText
import vdi.lib.config.common.interpolateFromEnv

fun StackConfig(path: Path): StackConfig {
  val validator = StackConfig::class.java.getResourceAsStream("/schema/config/stack-config.json")
    .use { JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012).getSchema(it)!! }

  val json = JSON.convertValue<ObjectNode>(Yaml().load<Any>(path.readText().interpolateFromEnv()))

  validator.validate(json)

  return JSON.convertValue(json)
}

var cachedConfig: StackConfig? = null

fun loadAndCacheStackConfig(path: Path = Path("/etc/vdi/config.yml")): StackConfig {
  return cachedConfig ?: StackConfig(path).also { cachedConfig = it }
}
