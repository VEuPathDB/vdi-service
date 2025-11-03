package vdi.config.parse.serde

private val pattern = Regex("\\$\\{(?i:env):(\\w+)(?::-([^}\$]+))?}")

fun String.interpolateFrom(env: Map<String, String>): String {
  val out = StringBuilder(length + length.ushr(2))

  var work = when (val p = indexOf("\${env:")) {
    -1   -> return this
    0    -> this
    else -> {
      out.append(this, 0, p)
      substring(p)
    }
  }

  val tail = when (val p = work.lastIndexOf('}')) {
    -1      -> return this
    in 0..7 -> return this
    else    -> {
      work = work.substring(0, p+1)
      work.substring(p+1)
    }
  }

  val tempBuffer = StringBuilder(256)
  work.lineSequence()
    .map {
      var line = it
      while (line.contains("\${env:")) {
        line.interpolateLine(tempBuffer, env)
        line = tempBuffer.toString()
        tempBuffer.clear()
      }
      line
    }
    .forEach(out::appendLine)

  out.append(tail)

  return out.toString()
}

private fun String.interpolateLine(buffer: StringBuilder, env: Map<String, String>) {
  var lastPos = 0

  for (match in pattern.findAll(this)) {
    buffer.append(this, lastPos, match.range.first)
    lastPos = match.range.last+1

    val (key, fallback) = match.destructured

    buffer.append(env[key]?.takeUnless { it.isEmpty() } ?: fallback)
  }

  buffer.append(this, lastPos, length)
}
