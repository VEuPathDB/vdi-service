package vdi.lib.db.app.model

enum class InstallType(val value: String) {
  Meta("meta"),
  Data("data"),
  ;

  companion object {
    @JvmStatic
    fun fromString(value: String): InstallType {
      return when (value) {
        Meta.value -> Meta
        Data.value -> Data
        else       -> throw IllegalArgumentException("unrecognized InstallType value: $value")
      }
    }
  }
}
