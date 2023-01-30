package vdi.conf

private const val DEFAULT_SERVER_PORT: UShort = 80u
private const val DEFAULT_SERVER_HOST: String = "0.0.0.0"

fun parseServerConfig(environment: MutableMap<String, String> = System.getenv()) =
  ServerConfiguration(
    port = environment["SERVER_PORT"]?.toUShort() ?: DEFAULT_SERVER_PORT,
    host = environment["SERVER_HOST"] ?: DEFAULT_SERVER_HOST
  )