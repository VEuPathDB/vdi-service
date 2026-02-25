package vdi.service.plugin.server.context

import io.ktor.http.content.PartData
import io.ktor.server.plugins.BadRequestException
import io.ktor.utils.io.asSource
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import vdi.service.plugin.util.safeFlush
import vdi.service.plugin.util.toKtxPath
import vdi.util.fs.TempFiles

internal fun PartData.handlePayload(workspace: Path, fileName: String): Path {
  val payload = Path(workspace, fileName)

  SystemFileSystem.sink(payload).use {
    val buf = it.buffered()
    when (this) {
      is PartData.BinaryChannelItem -> provider().asSource().buffered().transferTo(buf)
      is PartData.BinaryItem        -> provider().transferTo(buf)
      is PartData.FileItem          -> provider().asSource().buffered().transferTo(buf)
      is PartData.FormItem          -> buf.writeString(value)
    }
    buf.safeFlush()
  }

  return payload
}

internal suspend fun <T> withDoubleTempDirs(fn: suspend (Path, Path) -> T): T =
  TempFiles.withTempDirectory { dir1 -> TempFiles.withTempDirectory { dir2 -> fn(dir1.toKtxPath(), dir2.toKtxPath()) } }

@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalContracts::class)
internal inline fun reqNull(obj: Any?, key: String) {
  contract { returns() implies (obj == null) }
  if (obj != null)
    throw BadRequestException("form part \"$key\" was specified more than once in the request body")
}

@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalContracts::class)
internal inline fun reqNotNull(obj: Any?, key: String) {
  contract { returns() implies (obj != null) }
  if (obj == null)
    throw BadRequestException("missing required form part \"$key\"")
}
