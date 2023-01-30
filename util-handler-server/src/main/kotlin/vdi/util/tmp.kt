package vdi.util

import java.io.File


fun makeTempDirectory() : File {

}

inline fun withTempDirectory(fn: (tempDir: File) -> Unit) {
  val tempDir = makeTempDirectory()
  try {
    fn(tempDir)
  } finally {
    tempDir.deleteRecursively()
  }
}