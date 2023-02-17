package vdi.components.datasets

import java.io.InputStream
import java.time.OffsetDateTime

interface DatasetFileHandle {
  fun openStream(): InputStream

  fun getLastModified(): OffsetDateTime
}