package vdi.core.s3.files.docs

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import vdi.core.s3.files.LazyDatasetFileImpl

internal class LazyLoadDocument(path: String, bucket: ObjectContainer)
  : LazyDatasetFileImpl(path, bucket)
  , DocumentFile
{
  override val contentType: String
    get() = bucket[path]?.stat()?.contentType ?: super.contentType
}

