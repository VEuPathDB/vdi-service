package vdi.model.versioning

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import vdi.model.versioning.v1_0_0.JSONMigratorLegacy
import vdi.model.versioning.v1_7_0.JSONMigrator1_7_0

@Suppress("EnumEntryName")
enum class MetaStructureVersion {
  v1_0_0 {
    override val migrator: JSONMigrator
      get() = JSONMigratorLegacy
  },
  v1_7_0 {
    override val migrator: JSONMigrator
      get() = JSONMigrator1_7_0
  },
  ;

  @JsonValue
  override fun toString() = name.replace('_', '.')

  abstract val migrator: JSONMigrator

  companion object {
    @JvmStatic
    val CurrentVersion = v1_7_0

    @JvmStatic
    @JsonCreator
    fun fromString(raw: String) =
      fromStringOrNull(raw)
        ?: throw IllegalArgumentException("unrecognized ${MetaStructureVersion::class.qualifiedName} value '$raw'")

    @JvmStatic
    fun fromStringOrNull(raw: String) =
      raw.lowercase()
        .replace('.', '_')
        .let { str -> entries.firstOrNull { str == it.name } }
  }
}
