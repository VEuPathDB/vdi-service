package vdi.lib.db.app

import java.sql.SQLException

enum class AppDBPlatform(val platformString: String) {
  Postgres("postgresql") {
    val PostgresUniqueConstraintSQLState = "23505"

    override fun isUniqueConstraintViolation(e: Throwable): Boolean =
      (e is SQLException && e.sqlState == PostgresUniqueConstraintSQLState)
      || (e.cause?.let(::isUniqueConstraintViolation) ?: false)
  },

  Oracle("oracle") {
    val OracleUniqueConstraintErrorCode = 1

    override fun isUniqueConstraintViolation(e: Throwable): Boolean =
      (e is SQLException && e.errorCode == OracleUniqueConstraintErrorCode)
      || (e.cause?.let(::isUniqueConstraintViolation) ?: false)
  };

  companion object {
    fun fromString(value: String) =
      when (value.lowercase()) {
        "oracle"                 -> Oracle
        "postgres", "postgresql" -> Postgres
        else                     -> throw IllegalStateException("unrecognized AppDBPlatform value: $value")
      }
  }

  abstract fun isUniqueConstraintViolation(e: Throwable): Boolean
}
