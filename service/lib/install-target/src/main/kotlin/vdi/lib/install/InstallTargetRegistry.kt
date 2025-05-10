package vdi.lib.install

import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import org.veupathdb.vdi.lib.common.field.DataType
import vdi.lib.config.loadAndCacheStackConfig
import vdi.lib.err.StartupException

object InstallTargetRegistry {
  private val registry = HashMap<String, InstallTarget>(20)

  init {
    val uniqueSchemata = HashMap<Int, JsonSchema>(20)
    val schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012)

    loadAndCacheStackConfig().vdi.installTargets.forEach {
      val schema = uniqueSchemata.computeIfAbsent(it.datasetPropertySchema.hashCode()) { _ ->
        schemaFactory.getSchema(it.datasetPropertySchema.resolve().apply {
          // remove invalid JSON Schema keyword used for YAML anchors
          remove("sharedDefinitions")
        })
      }

      if (it.targetName in registry)
        throw StartupException("multiple install targets configured with the name ${it.targetName}")

      registry[it.targetName] = InstallTarget(
        it.enabled,
        it.targetName,
        it.dataTypes.mapTo(HashSet(it.dataTypes.size), DataType::of),
        schema,
      )
    }
  }

  operator fun get(name: String) = registry[name]

  operator fun iterator() = registry.values.iterator()

  operator fun contains(name: String) = name in registry
}

