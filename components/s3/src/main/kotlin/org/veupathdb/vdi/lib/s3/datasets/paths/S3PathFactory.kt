package org.veupathdb.vdi.lib.s3.datasets.paths

import org.veupathdb.vdi.lib.common.field.UserID

internal class S3PathFactory {
  fun rootDir() = S3Paths.rootDir()

  fun userDir(ownerID: UserID) = S3Paths.userDir(ownerID)
}