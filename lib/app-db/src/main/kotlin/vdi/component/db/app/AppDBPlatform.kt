package vdi.component.db.app

enum class AppDBPlatform(val platformString: String) {
  Postgres("postgresql"),
  Oracle("oracle");

  companion object {
    fun fromPlatformString(platformString: String): AppDBPlatform? {
      return entries.find { it.platformString === platformString }
    }
  }
}