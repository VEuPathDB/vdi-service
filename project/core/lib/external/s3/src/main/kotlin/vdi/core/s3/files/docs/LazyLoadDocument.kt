package vdi.core.s3.files.docs

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import vdi.core.s3.files.LazyObjectRef

internal class LazyLoadDocument(path: String, bucket: ObjectContainer): LazyObjectRef(path, bucket), DocumentFile
