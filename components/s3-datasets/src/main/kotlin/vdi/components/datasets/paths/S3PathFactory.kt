package vdi.components.datasets.paths

import vdi.components.common.fields.UserID

internal class S3PathFactory {
  fun rootDir() = S3Paths.rootDir()

  fun userDir(ownerID: UserID) = S3Paths.userDir(ownerID)
}