package vdi.components.common.util

fun Char.isHexDigit() =
  when (this) {
    in '0' .. '9',
    in 'A' .. 'F',
    in 'a' .. 'f' -> true
    else          -> false
  }

fun Char.toHexValue() =
  when (this) {
    in '0' .. '9' -> minus('0')
    in 'A' .. 'F' -> minus('A').plus(10)
    else          -> minus('a').plus(10)
  }

fun byteOf(first: Char, second: Char) =
  (first.toHexValue() shl 4 or second.toHexValue()).toByte()