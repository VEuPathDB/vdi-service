package vdi.util

import org.slf4j.Logger
import java.io.OutputStream

private const val ASCII_LF = 0x0A

class LoggingOutputStream(private val log: Logger) : OutputStream() {
  private val buffer = StringBuilder(1024)

  override fun write(b: Int) {
    if (b == ASCII_LF) {
      log.info(buffer.toString())
      buffer.clear()
    } else {
      buffer.append(b.toChar())
    }
  }

  override fun close() {
    if (buffer.isNotEmpty()) {
      log.info(buffer.toString())
      buffer.clear()
    }
  }
}