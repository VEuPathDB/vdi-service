package vdi.conf

import vdi.util.SecretString

private const val DB_NAME_PREFIX = "DB_CONNECTION_NAME_"
private const val DB_LDAP_PREFIX = "DB_CONNECTION_LDAP_"
private const val DB_USER_PREFIX = "DB_CONNECTION_USER_"
private const val DB_PASS_PREFIX = "DB_CONNECTION_PASS_"

private const val DB_ENV_VAR_INIT_CAPACITY = 12

fun parseDatabaseConfigs(environment: MutableMap<String, String> = System.getenv()) =
  environment.parse()

private fun MutableMap<String, String>.parse(): Map<String, DatabaseConfiguration> {
  val out = HashMap<String, DatabaseConfiguration>(DB_ENV_VAR_INIT_CAPACITY)

  keys.toList()
    .iterator()
    .forEach { key ->
      if (key !in this)
        return@forEach

      when {
        key.startsWith(DB_NAME_PREFIX) -> parse(key.substring(DB_NAME_PREFIX.length))
        key.startsWith(DB_LDAP_PREFIX) -> parse(key.substring(DB_LDAP_PREFIX.length))
        key.startsWith(DB_USER_PREFIX) -> parse(key.substring(DB_USER_PREFIX.length))
        key.startsWith(DB_PASS_PREFIX) -> parse(key.substring(DB_PASS_PREFIX.length))
        else                           -> null
      }?.also { out[it.name] = it }
    }

  if (out.isEmpty())
    throw RuntimeException("At least one set of database connection details must be provided.")

  return out
}

private fun MutableMap<String, String>.parse(suffix: String) =
  DatabaseConfiguration(
    name = remove(DB_NAME_PREFIX + suffix) ?: parsingFailed(suffix),
    ldap = remove(DB_LDAP_PREFIX + suffix)?.let(::SecretString) ?: parsingFailed(suffix),
    user = remove(DB_USER_PREFIX + suffix)?.let(::SecretString) ?: parsingFailed(suffix),
    pass = remove(DB_PASS_PREFIX + suffix)?.let(::SecretString) ?: parsingFailed(suffix),
  )

private fun parsingFailed(suffix: String): Nothing {
  throw RuntimeException(
    "One or more database connection configuration environment variables was not set with the suffix: $suffix"
  )
}