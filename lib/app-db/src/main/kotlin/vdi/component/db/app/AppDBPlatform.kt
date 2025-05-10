package vdi.component.db.app

enum class AppDBPlatform(val platformString: String) {
  Postgres("postgresql"),
  Oracle("oracle");

  companion object {
    fun fromString(value: String) =
      when (value.lowercase()) {
        "oracle"                 -> Oracle
        "postgres", "postgresql" -> Postgres
        else                     -> throw IllegalStateException("unrecognized AppDBPlatform value: $value")
      }
  }
}
