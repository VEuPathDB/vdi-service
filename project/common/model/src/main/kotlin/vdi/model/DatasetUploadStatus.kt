package vdi.model

enum class DatasetUploadStatus {
  Running,
  Success,
  Rejected,
  Failed,
  ;

  override fun toString() = name.lowercase()

  companion object {
    fun fromString(input: String) =
      when (input.lowercase()) {
        "success"  -> Success
        "rejected" -> Rejected
        "failed"   -> Failed
        "running"  -> Running
        else       -> throw IllegalArgumentException("invalid ${DatasetUploadStatus::class.simpleName} value: $input")
      }
  }
}