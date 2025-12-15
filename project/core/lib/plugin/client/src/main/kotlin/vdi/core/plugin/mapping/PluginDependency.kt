package vdi.core.plugin.mapping

import com.fasterxml.jackson.module.kotlin.convertValue
import kotlinx.coroutines.runBlocking
import vdi.config.loadManifestConfig
import vdi.core.health.Dependency
import vdi.core.plugin.client.PluginHandlerClient
import vdi.json.JSON
import vdi.logging.logger
import vdi.util.fn.leftOr

internal class PluginDependency(
  name: String,
  override val host: String,
  override val port: UShort,
  private val client: PluginHandlerClient,
): Dependency {
  private companion object {
    val Logger = logger<PluginDependency>()
  }

  override val name = "Plugin $name"

  override val protocol: String
    get() = "http"

  override var extra: Map<String, String> = emptyMap()
    private set

  override fun checkStatus(): Dependency.Status {
    extra = emptyMap()

    val response = try {
      runBlocking { client.getServerInfo() }
        .leftOr {
          Logger.error("plugin {} server returned server error {}", name, it.body.message)
          return Dependency.Status.NotOk
        }
    } catch (e: Throwable) {
      Logger.error("failed to fetch plugin server info for plugin {}", name, e)
      return Dependency.Status.NotOk
    }

    extra = JSON.convertValue(response.manifest)

    // get the git tag for the vdi server to check against the plugin server tag
    // (unless this is a dev build, in which case it doesn't matter)
    val tag = loadManifestConfig()
      .gitTag.takeUnless { it == "snapshot" || it == "unknown" }

    return when (tag) {
      null, // dev build
      response.manifest.gitTag -> Dependency.Status.Ok

      else -> {
        extra += "error" to "core server version does not match plugin server version: core = $tag, plugin = ${response.manifest.gitTag}"

        Dependency.Status.NotOk
      }
    }
  }
}