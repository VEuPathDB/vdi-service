package vdi.components.common

@JvmInline
value class DatasetID(val value: String) {
  init {
    if (value.length != 32)
      throw IllegalArgumentException("attempted to construct a DatasetID from a string that was not 32 characters in length: $value")
    for (c in value)
      when (c) {
        in '0' .. '9',
        in 'A' .. 'F',
        in 'a' .. 'f' -> {}
        else          -> throw IllegalArgumentException("attempted to construct a DatasetID from a string that contained an invalid character: $value")
      }
  }
}