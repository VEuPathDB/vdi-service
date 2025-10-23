package vdi.service.rest.util

enum class SupportedArchiveType {
  Zip {
    override val fileExtensions = arrayOf(".zip")
  },
  TarGZ {
    override val fileExtensions = arrayOf(".tgz", ".tar.gz")
  },
  ;

  abstract val fileExtensions: Array<String>

  fun matches(filename: String): Boolean =
    fileExtensions.any { filename.endsWith(it) }

  companion object {
    inline val SupportedExtensions
      get() = Zip.fileExtensions + TarGZ.fileExtensions
  }
}