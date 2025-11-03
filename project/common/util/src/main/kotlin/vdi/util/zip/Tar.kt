package vdi.util.zip

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import kotlin.io.path.*

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

  private inline fun TarArchiveInputStream.forEach(fn: (entry: TarArchiveEntry) -> Unit) {
    while (true)
      fn(nextEntry ?: break)
  }
}