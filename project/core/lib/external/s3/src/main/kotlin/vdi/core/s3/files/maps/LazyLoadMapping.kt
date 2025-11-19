package vdi.core.s3.files.maps

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import vdi.core.s3.files.LazyDatasetFileImpl

internal class LazyLoadMapping(path: String, bucket: ObjectContainer): LazyDatasetFileImpl(path, bucket), MappingFile
