package vdi.core.s3.files

import org.veupathdb.lib.s3.s34k.objects.S3Object

internal abstract class MaterializedStreamableFile(s3Object: S3Object): MaterializedObjectRef(s3Object)
