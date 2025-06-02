package vdi.util.fs

import java.nio.file.Path
import java.util.UUID
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.io.path.*

object TempFiles {

  private inline val TEMP_ROOT
    get() = Path.of("/tmp")

  fun makeTempPath(): Path =
    TEMP_ROOT.resolve(UUID.randomUUID().toString())

  fun makeTempDirectory() =
    makeTempPath().also { it.createDirectories() }

  /**
   * Creates a temporary file with the given file name in a new temp directory.
   *
   * Returns a pair containing the temp directory path and the temp file path.
   *
   * This directory must be manually deleted after it has served its purpose.
   *
   * @param fileName Name of the file to create.
   *
   * @return A pair containing the path to the temp directory as well as the
   * path to the temp file in that directory.
   */
  fun makeTempPath(fileName: String): Pair<Path, Path> {
    val dir = makeTempDirectory()
    val file = dir.resolve(fileName)
    return dir to file
  }

  fun makeTempFile() =
    makeTempPath().also { it.createFile() }

  @OptIn(ExperimentalContracts::class, ExperimentalPathApi::class)
  inline fun <T> withTempDirectory(fn: (directory: Path) -> T): T {
    contract { callsInPlace(fn, kotlin.contracts.InvocationKind.EXACTLY_ONCE) }

    val dir = makeTempDirectory()
    return try {
      fn(dir)
    } finally {
      dir.deleteRecursively()
    }
  }

  inline fun <T> withTempFile(fn: (file: Path) -> T): T {
    val file = makeTempFile()
    return try {
      fn(file)
    } finally {
      file.deleteIfExists()
    }
  }

  @OptIn(ExperimentalPathApi::class)
  inline fun <T> withTempPath(fn: (path: Path) -> T): T {
    val path = makeTempPath()
    return try {
      fn(path)
    } finally {
      if (path.exists()) {
        if (path.isDirectory())
          path.deleteRecursively()
        else
          path.deleteIfExists()
      }
    }
  }
}
