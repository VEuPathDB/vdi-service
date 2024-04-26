package vdi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logging

class Compose : Plugin<Project> {
  private enum class Target {
    Build,
    Start,
    Stop,
    Up,
    Down,
    ;

    fun execute(plg: Compose, project: Project) = when (this) {
      Build -> plg.build(project)
      Start -> plg.start()
      Stop -> plg.stop()
      Up -> plg.up()
      Down -> plg.down()
    }

    companion object {
      fun parse(target: String?) =
        when (val v = target?.lowercase()) {
          null    -> throw RuntimeException("no compose target specified")
          "build" -> Build
          "start" -> Start
          "stop"  -> Stop
          "up"    -> Up
          "down"  -> Down
          else    -> throw RuntimeException("unrecognized compose target: $v")
        }
    }
  }

  private val cmdBase = arrayOf(
    "docker", "compose",
    "-f", "docker-compose.yml",
    "-f", "docker-compose.dev.yml",
    "-f", "docker-compose.ssh.yml",
  )

  private val log = Logging.getLogger(javaClass)

  override fun apply(target: Project) {
    target.tasks.create("compose") {
      it.doLast {
        Target.parse(target.findProperty("compose-target") as String?)
          .execute(this, target)
      }
    }
  }

  private fun build(project: Project) {
    val username = project.requireProp("gpr.user", "GITHUB_USERNAME")
    val password = project.requireProp("gpr.key", "GITHUB_TOKEN")

    exec(
      arrayOf(*cmdBase,
        "build",
        "--build-arg=GITHUB_USERNAME=${username}",
        "--build-arg=GITHUB_TOKEN=${password}"
      ),
      {
        !it.contains("Downloading")
          && !it.contains("No history is available")
          && !it.contains("Found locally")
          && !it.contains("Build cache is disabled")
          && !it.contains("Transforming")
          && !it.contains("NO-SOURCE")
          && !it.contains("UP-TO-DATE")
      },
    )
  }

  private fun start() { exec(arrayOf(*cmdBase, "start")) }

  private fun stop() { exec(arrayOf(*cmdBase, "stop")) }

  private fun up() {
    exec(arrayOf(*cmdBase, "up", "-d"), { !it.contains("Starting") })
  }

  private fun down() { exec(arrayOf(*cmdBase, "down", "-v")) }

  private fun exec(
    args: Array<String>,
    stdFilter: (String) -> Boolean = { true },
    errFilter: (String) -> Boolean = ::warningFilter
  ) {
    with(ProcessBuilder(*args).start()) {
      runBlocking(Dispatchers.IO) {
        launch { inputStream.bufferedReader().use { it.lineSequence().filter(stdFilter).forEach(log::quiet) } }
        launch { errorStream.bufferedReader().use { it.lineSequence().filter(errFilter).forEach(log::quiet) } }
      }

      if (waitFor() != 0)
        throw RuntimeException("docker compose command failed")
    }
  }

  private fun Project.requireProp(key: String, env: String) =
    findProperty(key)
      ?: System.getenv(env)
      ?: throw RuntimeException("could not find prop $key or env var $env")

  private fun warningFilter(line: String) =
    !line.contains("level=warning")
}