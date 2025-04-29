package vdi.lib.config.common

private val pattern = Regex("\\$\\{(?i:env):(\\w+)(?::-(.+))?\\}")

fun String.interpolateFromEnv(): String {
  val out = StringBuilder(length + length.ushr(2))
  var lastPos = 0

  pattern.findAll(this)
    .forEach {
      out.append(this, lastPos, it.range.first)
      lastPos = it.range.last+1

      var (key, fallback) = it.destructured

      if (fallback.contains('$'))
        fallback = fallback.interpolateFromEnv()

      out.append(System.getenv(key)?.ifEmpty { fallback } ?: fallback)
    }

  out.append(this, lastPos, length)

  return out.toString()
}
