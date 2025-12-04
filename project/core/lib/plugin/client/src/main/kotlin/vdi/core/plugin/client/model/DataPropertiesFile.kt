package vdi.core.plugin.client.model

import java.io.InputStream

class DataPropertiesFile(
  val name: String,
  val stream: InputStream,
): AutoCloseable {
  operator fun component1() = name
  operator fun component2() = stream

  override fun close() = stream.close()
}
