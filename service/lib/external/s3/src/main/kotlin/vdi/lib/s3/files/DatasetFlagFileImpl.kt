package vdi.lib.s3.files

import java.io.InputStream
import java.time.OffsetDateTime

internal sealed class DatasetFlagFileImpl(
  path: String,
  existsChecker: () -> Boolean,
  lastModifiedSupplier: () -> OffsetDateTime?,
  loadObjectStream: () -> InputStream?,
  putObjectStream: (InputStream) -> Unit,
  private val toucher: () -> Unit,
)
  : DatasetFlagFile
  , DatasetFileImpl(path, existsChecker, lastModifiedSupplier, loadObjectStream, putObjectStream)
{
  override fun touch() {
    toucher()
  }
}
