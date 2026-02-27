package vdi.service.plugin.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.io.asInputStream
import kotlinx.io.asOutputStream
import kotlinx.io.buffered
import java.nio.file.Path as JavaPath
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import vdi.util.zip.unzip

internal fun Path.toJavaPath() =
  JavaPath.of(toString())

internal fun JavaPath.toKtxPath() =
  Path(toString())

/**
 * @see vdi.util.zip.unzip
 */
internal fun Path.unzip(into: Path) =
  toJavaPath().unzip(into.toJavaPath())

/**
 * @see java.nio.file.Path.resolve
 */
internal fun Path.resolve(child: String) =
  Path(this, child)

/**
 * @see kotlin.io.path.absolutePathString
 */
internal fun Path.absolutePathString() =
  SystemFileSystem.resolve(this).toString()

/**
 * @see kotlin.io.path.createDirectory
 */
internal fun Path.createDirectory() =
  SystemFileSystem.createDirectories(this)

/**
 * @see kotlin.io.path.deleteIfExists
 */
internal fun Path.deleteIfExists() =
  SystemFileSystem.delete(this)

/**
 * @see kotlin.io.path.deleteRecursively
 */
@OptIn(ExperimentalPathApi::class)
internal fun Path.deleteRecursively() =
  this.toJavaPath().deleteRecursively()

/**
 * @see kotlin.io.path.exists
 */
internal fun Path.exists() =
  SystemFileSystem.exists(this)

/**
 * @see kotlin.io.path.fileSize
 */
internal fun Path.fileSize() =
  SystemFileSystem.metadataOrNull(this)?.size

/**
 * @see kotlin.io.path.moveTo
 */
internal fun Path.moveTo(target: Path) = target.also { SystemFileSystem.atomicMove(this, it) }

/**
 * @see com.fasterxml.jackson.module.kotlin.readValue
 */
internal inline fun <reified T: Any> ObjectMapper.readValue(path: Path): T =
  SystemFileSystem.source(path).buffered().use { readValue(it.asInputStream()) }

internal fun ObjectMapper.writeValue(path: Path, content: Any) =
  SystemFileSystem.sink(path).buffered().use {
    writeValue(it.asOutputStream(), content)
    it.safeFlush()
  }