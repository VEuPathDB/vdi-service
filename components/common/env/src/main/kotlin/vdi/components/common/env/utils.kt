package vdi.components.common.env

import kotlin.time.Duration

typealias Environment = Map<String, String>

fun Environment.require(key: String): String =
  get(key)?.takeIf { it.isNotBlank() } ?: throw IllegalStateException("required environment variable $key is blank or absent")

fun Environment.optional(key: String): String? =
  get(key)?.takeIf { it.isNotBlank() }

fun Environment.optBool(key: String) =
  when (optional(key)?.lowercase()) {
    null                      -> null
    "true", "yes", "on", "1"  -> true
    "false", "no", "off", "0" -> false
    else                      -> throw IllegalStateException("environment variable $key could not be parsed as a boolean value")
  }


fun Environment.optInt(key: String) =
  try {
    optional(key)?.toInt()
  } catch (e: Throwable) {
    throw IllegalStateException("environment variable $key could not be parsed as an int value")
  }

fun Environment.optDuration(key: String) =
  try {
    optional(key)?.let(Duration.Companion::parse)
  } catch (e: Throwable) {
    throw IllegalStateException("environment variable $key could not be parsed as a duration value")
  }

fun Environment.optMap(key: String) =
  optional(key)
    ?.splitToSequence(',')
    ?.map { it.splitToPair(key) }
    ?.toMap()

private fun String.splitToPair(key: String): Pair<String, String> {
  if (isBlank())
    throw IllegalStateException("malformed map in environment variable $key")

  val it = splitToSequence(':', limit = 2)
    .iterator()

  if (!it.hasNext())
    throw IllegalStateException("malformed map in environment variable $key")

  val first = it.next()

  if (!it.hasNext())
    throw IllegalStateException("malformed map in environment variable $key")

  val second = it.next()

  return first to second
}
