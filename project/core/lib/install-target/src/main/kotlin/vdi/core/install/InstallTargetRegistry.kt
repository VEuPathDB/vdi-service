package vdi.core.install

import vdi.core.config.loadAndCacheStackConfig
import vdi.config.parse.ConfigurationException

object InstallTargetRegistry: Iterable<InstallTarget> {
  private val registry = HashMap<String, InstallTarget>(20)

  init {
    loadAndCacheStackConfig().vdi.installTargets.forEach {
      if (it.targetName in registry)
        throw ConfigurationException("multiple install targets configured with the name ${it.targetName}")

      registry[it.targetName] = InstallTarget(it.enabled, it.targetName, it.dataTypes, it.metaValidation)
    }
  }

  operator fun get(name: String) = registry[name]

  override fun iterator(): Iterator<InstallTarget> = registry.values.iterator()

  operator fun contains(name: String) = name in registry
}

