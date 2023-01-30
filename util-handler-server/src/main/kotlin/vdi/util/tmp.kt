package vdi.util

import java.io.File
import java.util.UUID


fun makeTempDirectory() : File {
  var tmpDir: File

  while (true) {
    val uuid = UUID.randomUUID().toString()
    tmpDir = File("/tmp/$uuid")

    if (tmpDir.exists())
      continue
    else
      break
  }

  tmpDir.mkdir()

  return tmpDir
}

inline fun withTempDirectory(fn: (tempDir: File) -> Unit) {
  val tempDir = makeTempDirectory()
  try {
    fn(tempDir)
  } finally {
    tempDir.deleteRecursively()
  }
}