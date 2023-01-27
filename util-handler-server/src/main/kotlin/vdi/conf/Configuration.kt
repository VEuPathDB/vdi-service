package vdi.conf

object Configuration {
  val serverConfiguration = parseServerConfig()

  val databaseConfigurations = parseDatabaseConfigs()
}

