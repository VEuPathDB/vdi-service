package vdi.components.common.util

import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteIfExists
import kotlin.io.path.deleteRecursively
import kotlin.io.path.isDirectory

@OptIn(ExperimentalPathApi::class)
inline fun Path.useThenDelete(fn: (path: Path) -> Unit) {
  try {
    fn(this)
  } finally {
    if (this.isDirectory())
      this.deleteRecursively()
    else
      this.deleteIfExists()
  }
}