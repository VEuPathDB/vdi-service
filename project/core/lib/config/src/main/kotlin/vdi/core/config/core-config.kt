@file:JvmName("Entrypoint")
package vdi.core.config

import java.nio.file.Path
import java.util.jar.Manifest
import kotlin.io.path.Path
import vdi.config.DefaultFullStackSchemaPath
import vdi.config.loadAndCastConfig
import vdi.config.makeDefaultConfigPath
import vdi.config.raw.ManifestConfig
import vdi.logging.MetaLogger


fun StackConfig(path: Path, schema: Path): StackConfig {
  MetaLogger.debug("attempting to load config '{}' and schema '{}'", path, schema)
  return loadAndCastConfig(path, schema)
}

private var cachedConfig: StackConfig? = null

fun loadAndCacheStackConfig(path: Path = makeDefaultConfigPath(), schema: Path = Path(DefaultFullStackSchemaPath)) =
  cachedConfig ?: StackConfig(path, schema).also { cachedConfig = it }

fun loadManifestConfig() =
  ManifestConfig::class.java.classLoader.getResourceAsStream("META-INF/MANIFEST.MF")
    .use(::Manifest)
    .let(::ManifestConfig)
