package vdi.util.zip

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import kotlin.io.path.*
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.outputStream

object Tar {
  fun decompressWithGZip(inputFile: Path, outputDir: Path) {
    if (!outputDir.exists())
      outputDir.createDirectories()
    if (!outputDir.isDirectory())
      throw IllegalStateException("target output path does not point to a directory")

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
  }

  fun entries(inputFile: Path, gzipped: Boolean): Sequence<Header> = sequence {
    TarArchiveInputStream(
      (if (gzipped)
        GZIPInputStream(inputFile.inputStream())
      else inputFile.inputStream())
        .buffered()
    ).use { tar ->
      for (entry in tar)
        yield(Header(entry.name, entry.realSize))
    }
  }

  data class Header(val name: String, val size: Long)
}