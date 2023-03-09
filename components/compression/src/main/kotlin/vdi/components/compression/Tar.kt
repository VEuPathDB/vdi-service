package vdi.components.compression

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.io.path.*

object Tar {
  private val log = LoggerFactory.getLogger(javaClass)

  fun compressWithGZip(output: Path, inputs: Collection<Path>) {
    log.trace("compressWithGZip(output={}, inputs={})", output, inputs)

    if (output.exists())
      throw IllegalStateException("target output path already exists!")

    log.debug("opening tar.gz output stream to path {}", output)
    TarArchiveOutputStream(GZIPOutputStream(output.outputStream().buffered())).use { tar ->
      inputs.forEach { file ->
        log.debug("compressing input file {}", file)
        tar.putArchiveEntry(TarArchiveEntry(file, file.name))
        file.inputStream().use { inp -> inp.transferTo(tar) }
        tar.closeArchiveEntry()
      }
    }
    log.debug("tar file written")
  }

  fun decompressWithGZip(inputFile: Path, outputDir: Path) {
    log.trace("decompressWithGZip(outputDir={})", outputDir)

    if (!outputDir.exists())
      outputDir.createDirectories()
    if (!outputDir.isDirectory())
      throw IllegalStateException("target output path does not point to a directory")

    log.debug("unpacking input tar.gz file {} into output directory {}", inputFile, outputDir)
    TarArchiveInputStream(GZIPInputStream(inputFile.inputStream().buffered())).use { tar ->
      tar.forEach { entry ->
        val target = outputDir.resolve(entry.name)

        if (entry.isDirectory) {
          target.createDirectories()
        } else if (entry.isFile) {
          if (target.exists()) {
            if (target.isDirectory())
              throw IllegalStateException("unable to unpack the file $target due to a directory already existing at that path")
            else
              throw IllegalStateException("unable to unpack the file $target due to a conflicting file already existing at that path")
          }

          target.parent.createDirectories()
          target.createFile()
          target.outputStream().use { os -> tar.transferTo(os) }
        }
      }
    }
    log.debug("unpacked tar file")
  }

  private inline fun TarArchiveInputStream.forEach(fn: (entry: TarArchiveEntry) -> Unit) {
    while (true)
      fn(nextTarEntry ?: break)
  }
}