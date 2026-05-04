package vdi.service.rest.config

@JvmInline
value class SupportedArchiveType private constructor(private val extensions: Array<String>) {
  fun matches(filename: String) = extensions.any(filename::endsWith)

  companion object {
    val ZIP
      get() = SupportedArchiveType(arrayOf(".zip"))

    val TAR_GZ
      get() = SupportedArchiveType(arrayOf(".tgz", ".tar.gz"))

    inline val entries
      get() = arrayOf(ZIP, TAR_GZ)

    fun getAllSupportedExtensions() = arrayOf(*ZIP.extensions, *TAR_GZ.extensions)
  }
}