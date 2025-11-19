package vdi.core.s3.files.flags

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import vdi.core.s3.files.LazyDatasetFileImpl

internal class LazyLoadRevisedFlag(path: String, bucket: ObjectContainer)
  : LazyDatasetFileImpl(path, bucket)
  , RevisedFlagFile
