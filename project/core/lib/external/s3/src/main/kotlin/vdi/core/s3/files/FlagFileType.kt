package vdi.core.s3.files

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class FlagFileType(@get:JsonValue val fileName: String) {
  Delete(FileName.DeleteFlagFile),
  Revised(FileName.RevisedFlagFile);

  companion object {
    @JvmStatic
    @JsonCreator
    fun fromString(value: String) = fromStringOrNull(value)
      ?: throw IllegalArgumentException("unrecognized ${FlagFileType::class.simpleName} value: $value")

    fun fromStringOrNull(value: String) = when(value) {
      Delete.fileName  -> Delete
      Revised.fileName -> Revised
      else             -> null
    }
  }
}
