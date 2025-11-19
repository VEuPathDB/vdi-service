package vdi.service.plugin.script

object ExitStatus {
  sealed interface Info {
    val code: UByte
    val displayName: String
  }

  data object Success: Info {
    override val code: UByte = 0u
    override val displayName: String = "success"
  }

  data object ValidationError: Info {
    override val code: UByte = 99u
    override val displayName: String = "validation_failure"
  }

  data object CompatibilityError: Info {
    override val code: UByte = 99u
    override val displayName: String = "incompatible"
  }

  class Unknown(override val code: UByte): Info {
    override val displayName: String
      get() = "unknown"

    override fun equals(other: Any?) = other is Unknown
    override fun hashCode() = code.hashCode()
  }
}