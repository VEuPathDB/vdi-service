package org.veupathdb.vdi.lib.db.app.model

enum class DeleteFlag(val value: Int) {
  NotDeleted(0),
  DeletedNotUninstalled(1),
  DeletedAndUninstalled(2),
  ;

  companion object {

    @JvmStatic
    fun fromInt(value: Int): DeleteFlag {
      for (enumValue in entries)
        if (value == enumValue.value)
          return enumValue

      throw IllegalArgumentException("unrecognized DeleteFlag value: $value")
    }
  }
}