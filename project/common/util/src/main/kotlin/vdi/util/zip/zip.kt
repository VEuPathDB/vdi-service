package vdi.util.zip

import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipFile
import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.io.path.*
import vdi.util.io.UncloseableInputStream

private val SingleZipHeader  = byteArrayOf(0x50, 0x4B, 0x03, 0x04)
private val EmptyZipHeader   = byteArrayOf(0x50, 0x4B, 0x05, 0x06)
private val SpannedZipHeader = byteArrayOf(0x50, 0x4B, 0x07, 0x08)


/**
 * Compresses the files at the given paths ([files]) into a new zip file at
 * the target [zipPath].
 *
 * @receiver Path for the zip file that will be created.
 *
 * @param files Paths to the target files that should be included in the built
 * zip file.
 *
 * @param level Compression level for the output zip.  Defaults to 9.
 *
 * @since 6.3.0
 */
fun Path.compress(files: Iterable<Path>, level: CompressionLevel = CompressionLevel(9u)) {
  val zip = ZipOutputStream(outputStream())
  zip.setLevel(level.value.toInt())

  for (file in files) {
    val entry = ZipEntry(file.name)
    zip.putNextEntry(entry)

    file.inputStream().transferTo(zip)
    zip.closeEntry()
  }

  zip.close()
}

/**
 * Unzips the target path into the given [into] directory.
 *
 * @receiver Path to the zip file to unzip.
 *
 * @param into Directory into which the zip file contents should be unzipped.
 *
 * **NOTE** This directory must exist and be empty at the time of this method
 * call.
 *
 * @param maxBytes Max count of decompressed bytes allowed to be read by the
 * given input stream.  Defaults to 10GiB.
 *
 * If a greater number of bytes than the given value would be decompressed,
 * this method will throw an [IllegalStateException].
 *
 * @return A collection of paths to the files that were unzipped.
 */
fun Path.unzip(into: Path, maxBytes: Long = 10737418240L): Collection<Path> {
  if (!into.exists())
    throw IllegalStateException("cannot unzip $this into non-existent path $into")

  if (!into.isDirectory())
    throw IllegalStateException("cannot unzip $this into non-directory path $into")

  if (into.listDirectoryEntries().isNotEmpty())
    throw IllegalStateException("refusing to unzip $this into non-empty directory $into")

  val unzipped = ArrayList<Path>(2)

  this.zipEntries(maxBytes).forEach { (entry, zipStream) ->
    val target = into.resolve(entry.name)

    if (entry.isDirectory) {
      target.createDirectories()
    } else {
      if (target.parent != into)
        target.parent.createDirectories()

      target.outputStream().use { outStream -> zipStream.transferTo(outStream) }

      unzipped.add(target)
    }
  }

  return unzipped
}

/**
 * Returns a sequence of [ZipEntry] instances paired with an [InputStream]
 * that may be used to stream out the contents of the zip entry itself.
 *
 * **WARNING**: The [InputStream]s in the returned sequence must be consumed
 * or skipped entirely before processing the next entry in the sequence.  This
 * is due to the way the underlying [ZipInputStream] functions.  If an
 * [InputStream] is consumed _after_ proceeding to the next sequence entry, it
 * will return the contents of the next [InputStream] rather than the contents
 * of the stream that was originally returned.
 *
 * @receiver Path to the zip file whose entries should be sequenced.
 *
 * @param maxBytes Max count of decompressed bytes allowed to be read by the
 * given input stream.  Defaults to 10GiB.
 *
 * If a greater number of bytes than the given value would be decompressed,
 * this method will throw an [IllegalStateException].
 *
 * @return A sequence of [ZipEntry] and [InputStream] instances contained in
 * the target zip file.
 */
fun Path.zipEntries(maxBytes: Long = 10737418240L): Sequence<Pair<ZipEntry, InputStream>> {
  if (!exists())
    throw FileNotFoundException("cannot open non-existent zip file ${this}")

  if (isDirectory())
    throw IllegalStateException("cannot open target zip file ${this} as it is a directory")

  if (!isReadable())
    throw IllegalStateException("cannot open unreadable zip file ${this}")

  return sequence {
    inputStream().use { stream ->
      for (value in stream.zipEntries(maxBytes))
        yield(value)
    }
  }
}

/**
 * Returns the type of Zip archive the target path points to.
 *
 * If the target points to a file that is not a valid zip archive, the value
 * [ZipType.Invalid] will be returned.
 *
 * @receiver Target path to test.
 *
 * @return Zip type for the target path, or [ZipType.Invalid] if the target
 * path is not a valid Zip archive.
 */
fun Path.getZipType(): ZipType {
  if (!isRegularFile())
    return ZipType.Invalid

  return inputStream(StandardOpenOption.READ).use { bs ->
    val buffer = ByteArray(4)

    if (bs.read(buffer) != 4)
      ZipType.Invalid
    else if (buffer.contentEquals(SingleZipHeader))
      ZipType.Standard
    else if (buffer.contentEquals(EmptyZipHeader))
      ZipType.Empty
    else if (buffer.contentEquals(SpannedZipHeader))
      ZipType.Spanned
    else
      ZipType.Invalid
  }
}

/**
 * Returns a sequence of [ZipEntry] instances paired with an [InputStream]
 * that may be used to stream out the contents of the zip entry itself.
 *
 * **WARNING**: The [InputStream]s in the returned sequence must be consumed
 * or skipped entirely before processing the next entry in the sequence.  This
 * is due to the way the underlying [ZipInputStream] functions.  If an
 * [InputStream] is consumed _after_ proceeding to the next sequence entry, it
 * will return the contents of the next [InputStream] rather than the contents
 * of the stream that was originally returned.
 *
 * @receiver Input stream containing the zip file contents to unzip.
 *
 * @param maxBytes Max count of decompressed bytes allowed to be read by the
 * given input stream.  Defaults to 10GiB.
 *
 * If a greater number of bytes than the given value would be decompressed,
 * this method will throw an [IllegalStateException].
 *
 * @return A sequence of [ZipEntry] and [InputStream] instances contained in
 * the target zip file.
 */
fun InputStream.zipEntries(maxBytes: Long = 10737418240L): Sequence<Pair<ZipEntry, InputStream>> = sequence {
  ZipFile.builder().setInputStream(this@zipEntries).get().use {
    var byteCount = 0L
    for (entry in it.entries) {
      byteCount += entry.size
      if (entry.size > maxBytes || byteCount > maxBytes)
        throw IllegalStateException("attempted to decompress more than $maxBytes bytes from a given zip stream")

      yield(entry to it.getInputStream(entry))
    }
  }
}