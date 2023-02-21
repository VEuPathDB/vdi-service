package vdi.components.datasets.paths

internal class S3PathFactory(root: String) {
  private val root = root.trim('/')

  fun rootDir() = S3Paths.rootDir(root)

  fun userDir(ownerID: String) = S3Paths.userDir(root, ownerID)
}