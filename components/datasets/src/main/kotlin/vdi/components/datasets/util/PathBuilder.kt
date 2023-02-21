package vdi.components.datasets.util

internal class PathBuilder(root: String, segments: Int = 8, private val delimiter: String = "/") {
  private val segments = arrayOfNulls<String>(segments).also { it[0] = root }
  private var index = 1

  operator fun div(segment: String): PathBuilder {
    if (index == segments.size)
      throw IllegalStateException("attempted to assemble a path with more segments than were allocated for (allocated size = ${segments.size}, current path = )")

    segments[index++] = segment

    return this
  }

  override fun toString(): String {
    val sb = StringBuilder(calcSize())

    sb.append(segments[0])

    for (i in 1 until index)
      sb.append(delimiter)
        .append(segments[i])

    return sb.toString()
  }

  private fun calcSize(): Int {
    var size = segments[0]!!.length

    for (i in 1 until index)
      size += delimiter.length + segments[i]!!.length

    return size
  }
}