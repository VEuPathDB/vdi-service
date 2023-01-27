package vdi.conf

fun parseServerConfig(environment: MutableMap<String, String> = System.getenv()) =
  ServerConfiguration(
    port = environment["SERVER_PORT"]?.toUShort() ?: 80u
  )