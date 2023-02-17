package vdi.components.datasets

import java.io.InputStream

interface DatasetFileHandle {
  fun openStream(): InputStream
}