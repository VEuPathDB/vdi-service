package vdi.service.rest.util

fun ULong.toFileSizeString(): String =
  Formatting.formatFileSize(toLong())
