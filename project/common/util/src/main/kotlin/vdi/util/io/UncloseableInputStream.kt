package vdi.util.io

import java.io.InputStream

class UncloseableInputStream(private val rawStream: InputStream): InputStream() {
  override fun read() = rawStream.read()
  override fun read(b: ByteArray) = rawStream.read(b)
  override fun read(b: ByteArray, off: Int, len: Int) = rawStream.read(b, off, len)
  override fun close() {}
}
