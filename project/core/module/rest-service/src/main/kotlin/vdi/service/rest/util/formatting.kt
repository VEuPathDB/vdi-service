package vdi.service.rest.util

import java.text.DecimalFormat

private val SizeFormat = DecimalFormat("#.#")

private const val GiB = 1073741824L;
private const val MiB = 1048576L;
private const val KiB = 1024L;

fun ULong.toFileSizeString() = toLong().toFileSizeString()

fun Long.toFileSizeString(): String = when {
  this >= GiB -> SizeFormat.format(toDouble() / GiB) + "GiB"
  this >= MiB -> SizeFormat.format(toDouble() / MiB) + "MiB"
  this >= KiB -> SizeFormat.format(toDouble() / KiB) + "KiB"
  else -> toString() + "B"
}