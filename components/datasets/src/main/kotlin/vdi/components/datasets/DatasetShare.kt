package vdi.components.datasets

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

interface DatasetShare {
  val recipientID: Long

  fun getGrantState(): GrantState

  fun setGrantState(state: GrantState)

  fun getReceiptState(): ReceiptState

  fun setReceiptState(state: ReceiptState)

  enum class GrantState {
    Granted,
    Revoked,
    ;

    @JsonValue
    override fun toString() = name.lowercase()

    companion object {
      @JvmStatic
      @JsonCreator
      fun fromString(value: String): GrantState {
        val tgt = value.lowercase()

        for (v in values())
          if (tgt == v.toString())
            return v

        throw IllegalArgumentException("unrecognized GrantState value $value")
      }
    }
  }

  enum class ReceiptState {
    Accepted,
    Rejected,
    ;

    @JsonValue
    override fun toString() = name.lowercase()

    companion object {
      @JvmStatic
      @JsonCreator
      fun fromString(value: String): ReceiptState {
        val tgt = value.lowercase()

        for (v in values())
          if (tgt == v.toString())
            return v

        throw IllegalArgumentException("unrecognized ReceiptState value $value")
      }
    }
  }
}