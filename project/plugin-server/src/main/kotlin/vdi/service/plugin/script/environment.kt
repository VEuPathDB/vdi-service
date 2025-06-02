package vdi.service.plugin.script

/**
 * Array of environment variables that are 'safe' to pass through to the plugin
 * scripts.
 *
 * Any environment variable that does not appear in this array will not be
 * passed through from the server environment to the plugin environment.
 */
private val SafeKeys = arrayOf(
  "_",
  "GUS_HOME",
  "HOSTNAME",
  "JAVA_HOME",
  "LANG",
  "LD_LIBRARY_PATH",
  "ORACLE_HOME",
  "PATH",
  "PROJECT_HOME",
  "PYTHONPATH",
  "SITE_BUILD",
  "TEMPLATE_DB_NAME",
  "TEMPLATE_DB_USER",
  "TEMPLATE_DB_PASS",
  "TZ",
)

private val BaseMap: Map<String, String> = HashMap<String, String>(SafeKeys.size)
  .apply { SafeKeys.forEach { key -> System.getenv(key)?.also { put(key, it) } } }

/**
 * Creates a mutable copy of the safe environment variables.
 */
fun cloneEnvironment(): MutableMap<String, String> = HashMap<String, String>(24).apply { putAll(BaseMap) }
