package vdi.conf

import vdi.util.SecretString

private const val DB_NAME_PREFIX = "DB_CONNECTION_NAME_"
private const val DB_LDAP_PREFIX = "DB_CONNECTION_LDAP_"
private const val DB_USER_PREFIX = "DB_CONNECTION_USER_"
private const val DB_PASS_PREFIX = "DB_CONNECTION_PASS_"

private const val DB_ENV_VAR_INIT_CAPACITY = 12

fun parseDatabaseConfigs(environment: MutableMap<String, String> = System.getenv()) =
  environment.parse()

private fun Map<String, String>.parse(): Map<String, DatabaseConfiguration> {
  val out  = HashMap<String, DatabaseConfiguration>(DB_ENV_VAR_INIT_CAPACITY)
  val seen = HashSet<String>(12)

  forEach { (key, value) ->
    if (key in seen)
      return@forEach

    when {
      key.startsWith(DB_NAME_PREFIX) -> parse(key.substring(DB_NAME_PREFIX.length), seen, out)
      key.startsWith(DB_LDAP_PREFIX) -> parse(key.substring(DB_LDAP_PREFIX.length), seen, out)
      key.startsWith(DB_USER_PREFIX) -> parse(key.substring(DB_USER_PREFIX.length), seen, out)
      key.startsWith(DB_PASS_PREFIX) -> parse(key.substring(DB_PASS_PREFIX.length), seen, out)
    }
  }

  if (out.isEmpty())
    throw RuntimeException("At least one set of database connection details must be provided.")

  return out
}

private fun Map<String, String>.parse(
  suffix: String,
  seen:   MutableSet<String>,
  out:    MutableMap<String, DatabaseConfiguration>
) {
  val db = parse(suffix)

  seen.add(DB_NAME_PREFIX + suffix)
  seen.add(DB_LDAP_PREFIX + suffix)
  seen.add(DB_USER_PREFIX + suffix)
  seen.add(DB_PASS_PREFIX + suffix)

  out[db.name] = db
}

private fun Map<String, String>.parse(suffix: String) =
  DatabaseConfiguration(
    name = get(DB_NAME_PREFIX + suffix) ?: parsingFailed(suffix),
    ldap = get(DB_LDAP_PREFIX + suffix)?.let(::SecretString) ?: parsingFailed(suffix),
    user = get(DB_USER_PREFIX + suffix)?.let(::SecretString) ?: parsingFailed(suffix),
    pass = get(DB_PASS_PREFIX + suffix)?.let(::SecretString) ?: parsingFailed(suffix),
  )

private fun parsingFailed(suffix: String): Nothing {
  throw RuntimeException(
    "One or more database connection configuration environment variables was not set with the suffix: $suffix"
  )
}