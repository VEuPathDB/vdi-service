@file:Suppress("NOTHING_TO_INLINE")
package vdi.service.rest.server.inputs

inline fun Int.toSafeLimit(defaultLimit: UInt) =
  when {
    this == 0 -> defaultLimit
    this < 0  -> 0u
    else      -> toUInt()
  }

inline fun Int.toSafeOffset(rawLimit: Int) =
  when {
    this < 1 || rawLimit < 0 -> 0u
    else                     -> toUInt()
  }
