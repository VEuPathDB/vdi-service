package vdi.module.migrations

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.postgresql.Driver
import vdi.core.config.vdi.CacheDBConfig
import vdi.logging.MetaLogger

fun migrateInternalDatabase(conf: CacheDBConfig) {
  MetaLogger.info("testing for required internal database migrations")
  val ds = HikariConfig().apply {
      jdbcUrl = "jdbc:postgresql://${conf.server.host}:${conf.server.port}/${conf.name}"
      username = conf.username
      password = conf.password.asString
      maximumPoolSize = 5
      driverClassName = Driver::class.qualifiedName
    }
    .let(::HikariDataSource)

  ds.connection.use { conn ->
    val dbVersion = conn.checkVersion()

    MetaLogger.info("current internal database version is {}", dbVersion)

    var migrated = false;

    getMigrations(dbVersion)
      .onEach { migrated = true }
      .onEach { MetaLogger.info("applying migration to version {}", it.version) }
      .flatMap { it.getQueries() }
      .onEach { MetaLogger.debug("migration query: {}", it) }
      .forEach { conn.createStatement().use { st -> st.execute(it) } }

    if (migrated)
      MetaLogger.info("internal database migration complete")
  }
}
