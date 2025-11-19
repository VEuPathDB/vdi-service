package vdi.model.meta

/**
 * Dataset Identifier
 *
 * An opaque identifier that is unique to a single VDI dataset.
 *
 * @since 1.0.0
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
@JvmInline
value class DatasetID(val asString: String) {
  inline val hasRevisionCounter
    get() = asString.contains('.')

  inline val revisionCounter
    get() = asString.substringAfterLast('.', "0").toInt()

  inline val rootID
    get() = asString.substringBefore('.')

  fun nextRevision() = DatasetID("$rootID.${revisionCounter+1}")

  override fun toString() = asString
}
