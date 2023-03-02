package vdi.components.datasets.paths

internal class S3PathFactory {
  fun rootDir() = S3Paths.rootDir()

  fun userDir(ownerID: String) = S3Paths.userDir(ownerID)
}