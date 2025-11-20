package vdi.core.s3.files.shares

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.core.s3.files.MaterializedObjectRef
import vdi.json.JSON
import vdi.model.meta.DatasetShareOffer
import vdi.model.meta.UserID

internal class MaterializedShareOffer(
  override val recipientID: UserID,
  s3Object: S3Object,
): MaterializedObjectRef(s3Object), ShareOffer {
  override val contentType: String
    get() = super<ShareOffer>.contentType

  override fun load(): DatasetShareOffer? =
    bucket.open(path)?.stream?.use { JSON.readValue(it) }
}