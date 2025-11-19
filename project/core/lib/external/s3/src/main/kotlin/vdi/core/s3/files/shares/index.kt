package vdi.core.s3.files.shares

import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import org.veupathdb.lib.s3.s34k.objects.S3Object
import vdi.model.meta.UserID

fun ShareOffer(recipientID: UserID, path: String, bucket: ObjectContainer): ShareOffer =
  LazyLoadShareOffer(recipientID, path, bucket)

fun ShareOffer(recipientID: UserID, s3Object: S3Object): ShareOffer =
  MaterializedShareOffer(recipientID, s3Object)

fun ShareReceipt(recipientID: UserID, path: String, bucket: ObjectContainer): ShareReceipt =
  LazyLoadShareReceipt(recipientID, path, bucket)

fun ShareReceipt(recipientID: UserID, s3Object: S3Object): ShareReceipt =
  MaterializedShareReceipt(recipientID, s3Object)