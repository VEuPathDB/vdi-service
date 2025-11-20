package vdi.core.s3.files.docs

import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.core.s3.files.MaterializedObjectRef

internal class MaterializedDocument(s3Object: S3Object): MaterializedObjectRef(s3Object), DocumentFile