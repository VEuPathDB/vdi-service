package vdi.core.db.cache

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import javax.sql.DataSource
import vdi.core.config.loadAndCacheStackConfig
import vdi.core.db.cache.health.DatabaseDependency
import vdi.core.err.StartupException
import vdi.core.health.Dependency
import vdi.core.health.RemoteDependencies
import vdi.logging.MetaLogger
import org.postgresql.Driver as PostgresDriver

internal object CacheDBImpl: CacheDB, CacheDBImplBase() {
  override val details: CacheDBConnectionDetails

  override val dataSource: DataSource = loadAndCacheStackConfig().vdi.cacheDB.let {
    MetaLogger.info("initializing datasource for cache-db")

    details = CacheDBConnectionDetails(it)

    HikariConfig()
      .apply {
        jdbcUrl  = makeJDBCPostgresConnectionString(details.host, details.port, details.name)
        username = it.username
        password = it.password.asString
        maximumPoolSize = it.poolSize?.toInt() ?: 5
        driverClassName = PostgresDriver::class.java.name
      }
      .let(::HikariDataSource)
  }

  override val connection: Connection
    get() = dataSource.connection

  init {
    RemoteDependencies.register(DatabaseDependency(this).also {
      when (it.checkStatus()) {
        Dependency.Status.NotOk,
        Dependency.Status.Unknown -> throw StartupException("could not connect to cache db")
        else                      -> {}
      }
    })
  }
}

