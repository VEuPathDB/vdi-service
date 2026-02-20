package vdi.core.db.app

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource
import kotlin.time.Duration
import vdi.db.app.TargetDBPlatform
import vdi.db.app.TargetDatabaseDetails
import vdi.model.field.HostAddress
import vdi.model.field.SecretString

internal class TargetDatabaseConfig(
  val name: String,
  val server: HostAddress,
  val user: String,
  val pass: SecretString,
  val platform: TargetDBPlatform,
  var poolSize: UByte,
  var idleTimeout: Duration,
) {
  constructor(details: TargetDatabaseDetails): this(
    details.name,
    details.server,
    details.user,
    details.pass,
    details.platform,
    details.poolSize,
    details.idleTimeout,
  )

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is TargetDatabaseConfig) return false

    if (name != other.name) return false
    if (server != other.server) return false
    if (user != other.user) return false
    if (pass != other.pass) return false
    if (platform != other.platform) return false

    return true
  }

  override fun hashCode(): Int {
    var result = server.hashCode()
    result = 31 * result + name.hashCode()
    result = 31 * result + user.hashCode()
    result = 31 * result + pass.hashCode()
    result = 31 * result + platform.hashCode()
    return result
  }

  fun makeDataSource(): DataSource =
    HikariConfig()
      .also {
        it.jdbcUrl = toJDBCString()
        it.username = user
        it.password = pass.asString
        it.maximumPoolSize = poolSize.toInt()
        it.driverClassName = driverClass
        it.idleTimeout = idleTimeout.inWholeMilliseconds
      }
      .let(::HikariDataSource)

  private fun toJDBCString() =
    when (platform) {
      TargetDBPlatform.Postgres -> "jdbc:postgresql://$server/$name"
      TargetDBPlatform.Oracle   -> "jdbc:oracle:thin:@//$server/$name"
    }

  private inline val driverClass get() =
    when (platform) {
      TargetDBPlatform.Postgres -> "org.postgresql.Driver"
      TargetDBPlatform.Oracle   -> "oracle.jdbc.OracleDriver"
    }
}
