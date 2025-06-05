package vdi.model.data


@Suppress("NOTHING_TO_INLINE")
inline fun String.toDatasetID() = DatasetID(this)

@Deprecated("dataset ID no longer has any validation, this never returns null", replaceWith = ReplaceWith("toDatasetID"))
fun String.toDatasetIDOrNull() = toDatasetID()

fun Long.toUserID() = UserID(this)
fun Long.toUserIDOrNull() = try { UserID(this) } catch (e: Throwable) { null }

fun String.toUserID() = UserID(this)
fun String.toUserIDOrNull() = try { UserID(this) } catch (e: Throwable) { null }
