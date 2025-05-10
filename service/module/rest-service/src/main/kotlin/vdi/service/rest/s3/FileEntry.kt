package vdi.service.rest.s3

import org.veupathdb.lib.s3.s34k.objects.S3Object

data class FileEntry(
  val name: String,
  val size: Long,
) {
  constructor(obj: S3Object) : this(obj.baseName, obj.stat()!!.size)
}
