import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

tasks {
  fun getGitHubUsername() = findProperty("github.username") as String?
    ?: System.getenv("GH_USERNAME")
    ?: System.getenv("GITHUB_USERNAME")?.also { logger.error("GITHUB_USERNAME env var is deprecated in favor of GH_USERNAME or setting 'github.username' in a global gradle.properties file") }
    ?: throw Exception("github username not set")

  fun getGithubPassword() = findProperty("github.token") as String?
    ?: System.getenv("GH_TOKEN")
    ?: System.getenv("GITHUB_TOKEN")?.also { logger.error("GH_TOKEN env var is deprecated in favor of GH_TOKEN or setting 'github.token' in a global gradle.properties file") }
    ?: throw Exception("github token not set")

  fun generateCommand(censor: Boolean): List<String> {
    val ghUsername = if (censor) "\"\${GH_USERNAME}\"" else getGitHubUsername()
    val ghToken = if (censor) "\"\${GH_TOKEN}\"" else getGithubPassword()

    val tag = with(ProcessBuilder("git", "describe", "--tags").start()) {
      when (waitFor()) {
        0    -> inputStream.readAllBytes().decodeToString().trim()
        else -> "snapshot"
      }
    }

    val branch = with(ProcessBuilder("git", "branch", "--show-current").start()) {
      when (waitFor()) {
        0    -> inputStream.readAllBytes().decodeToString().trim()
        else -> "unknown"
      }
    }

    val dirty = ProcessBuilder("git", "diff", "--quiet").start().waitFor() != 0

    val commit = ProcessBuilder("git", "rev-parse", "HEAD").start()
      .let { it.waitFor(); it.inputStream.readAllBytes().decodeToString().trim() }
      .let { if (dirty) "$it-dirty" else it }

    val timestamp = ZonedDateTime.now(ZoneOffset.UTC)
      .format(DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral(' ')
        .appendValue(ChronoField.HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
        .appendOffsetId()
        .toFormatter())
      .let { if (censor) "\"$it\"" else it }

    val buildID = System.getProperty("user.name") + (System.currentTimeMillis()/1000).toString().substring(5)

    return listOf(
      "docker", "build",
      "--tag=veupathdb/vdi-service:latest",
      "--file=project/core/Dockerfile",
      "--build-arg=GH_USERNAME=${ghUsername}",
      "--build-arg=GH_TOKEN=${ghToken}",
      "--build-arg=GIT_TAG=$tag",
      "--build-arg=GIT_URL=localhost",
      "--build-arg=GIT_BRANCH=$branch",
      "--build-arg=GIT_COMMIT=$commit",
      "--build-arg=BUILD_TIME=$timestamp",
      "--build-arg=BUILD_ID=$buildID",
      "--build-arg=BUILD_NUMBER=n/a",
      ".",
    )
  }

  register("build-image-cmd") {
    doLast {
      println()
      println(generateCommand(true).joinToString(" \\\n  "))
      println()
    }
  }

  register("build-image") {
    doLast {
      val status = ProcessBuilder(generateCommand(false))
        .apply { environment().putAll(System.getenv()) }
        .directory(projectDir)
        .redirectError(projectDir.resolve("docker-build.stderr.log"))
        .redirectOutput(projectDir.resolve("docker-build.stdout.log"))
        .start()
        .waitFor()

      if (status != 0) {
        throw Exception("docker build failed")
      }
    }
  }
}