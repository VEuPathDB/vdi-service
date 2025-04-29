package vdi.lib.config

import com.fasterxml.jackson.module.kotlin.convertValue
import org.veupathdb.vdi.lib.json.JSON
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path
import kotlin.io.path.readText
import vdi.lib.config.common.interpolateFromEnv

fun StackConfig(path: Path): StackConfig {
  return JSON.convertValue(Yaml().load<Any>(path.readText().interpolateFromEnv()))
}
