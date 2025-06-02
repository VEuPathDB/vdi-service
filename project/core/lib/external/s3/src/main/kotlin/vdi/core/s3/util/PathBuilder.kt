package vdi.core.s3.util

/**
 * Path Builder
 *
 * Utility class for building a path by appending segments to a mutable
 * collection.
 *
 * @constructor Constructs a new `PathBuilder` instance.
 *
 * @param root Root path segment.
 *
 * @param segments Maximum number of path segments the path builder can contain.
 *
 * @param delimiter Path segment delimiter.
 */
internal class PathBuilder(root: String, segments: Int = 8, private val delimiter: String = "/") {
  private val segments = arrayOfNulls<String>(segments).also { it[0] = root }
  private var index = 1

  fun append(segment: String): PathBuilder {
    if (index == segments.size)
      throw IllegalStateException("attempted to assemble a path with more segments than were allocated for (allocated size = ${segments.size}, current path = )")

    segments[index++] = segment

    return this
  }

  fun dirPath() = append("").toString()

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
