package vdi.db.app

import java.sql.SQLException

enum class TargetDBPlatform {
  Postgres {
    private val PostgresUniqueConstraintSQLState = "23505"
    private val PostgresForeignKeyViolationSQLState = "23503"

    override fun isUniqueConstraintViolation(e: Throwable): Boolean =
      (e is SQLException && e.sqlState == PostgresUniqueConstraintSQLState)
      || (e.cause?.let(::isUniqueConstraintViolation) ?: false)

    override fun isForeignKeyConstraintViolation(e: Throwable): Boolean =
      (e is SQLException && e.sqlState == PostgresForeignKeyViolationSQLState)
      || (e.cause?.let(::isForeignKeyConstraintViolation) ?: false)
  },

  Oracle {
    private val OracleUniqueConstraintErrorCode = 1

    override fun isUniqueConstraintViolation(e: Throwable): Boolean =
      (e is SQLException && e.errorCode == OracleUniqueConstraintErrorCode)
      || (e.cause?.let(::isUniqueConstraintViolation) ?: false)

    override fun isForeignKeyConstraintViolation(e: Throwable): Boolean {
      throw UnsupportedOperationException()
    }
  };

  inline val defaultPort: UShort get() = when (this) {
    Postgres -> 5432u
    Oracle   -> 1521u
  }

  companion object {
    fun fromString(value: String) =
      when (value.lowercase()) {
        "oracle"                 -> Oracle
        "postgres", "postgresql" -> Postgres
        else                     -> throw IllegalStateException("unrecognized AppDBPlatform value: $value")
      }
  }

  abstract fun isUniqueConstraintViolation(e: Throwable): Boolean

  abstract fun isForeignKeyConstraintViolation(e: Throwable): Boolean
}
