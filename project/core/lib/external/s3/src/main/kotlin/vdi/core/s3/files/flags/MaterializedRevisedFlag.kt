package vdi.core.s3.files.flags

import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.core.s3.files.EagerDatasetFileImpl

internal class MaterializedRevisedFlag(s3Object: S3Object)
  : EagerDatasetFileImpl(s3Object)
  , RevisedFlagFile