package vdi.module.migrations

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

private const val MigrationPath = "db-migrations/"

internal fun getMigrations(after: Int) =
  getAllMigrations().filter { it.version > after }

internal data class Migration(val version: Int, val scripts: List<String>): Comparable<Migration> {
  override fun compareTo(other: Migration) =
    version.compareTo(other.version)

  fun getQueries() =
    scripts.asSequence()
      .map { javaClass.getResourceAsStream(it)!!.use { stream -> stream.readAllBytes() } }
      .map { it.decodeToString() }
}

private fun getAllMigrations(): Sequence<Migration> {
  var lastVersion = 0
  val buffer = ArrayList<String>(10)

  return sequence {
    zipStream()
      .filter { it.name.startsWith(MigrationPath) && it.name.endsWith(".sql") }
      .forEach { entry ->
        val version = entry.getMigrationVersion()

        if (version == lastVersion) {
          buffer.add(entry.name)
        } else {
          yield(Migration(lastVersion, buffer.sorted()))
          lastVersion = version
          buffer.clear()
        }
      }
  }
}

private fun ZipEntry.getMigrationVersion() =
  name.substring(MigrationPath.length, name.indexOf('/', MigrationPath.length))
    .trim('0')
    .toInt()

private fun zipStream() = sequence {
  ZipInputStream(jarLocation().openStream()).use { zip ->
    while (true)
      yield(zip.nextEntry ?: break)
  }
}

private fun jarLocation() =
  object{}.javaClass.protectionDomain.codeSource.location!!

