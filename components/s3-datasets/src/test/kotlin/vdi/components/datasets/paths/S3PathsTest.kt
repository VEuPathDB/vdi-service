package vdi.components.datasets.paths

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

private val USER_ID = UserID("b")
private val DATASET_ID = DatasetID("912ec803b2ce49e4a541068d495ab570")
private val RECIPIENT_ID = UserID("d")
private const val FILE_NAME = "file.ext"


@DisplayName("S3Paths")
class S3PathsTest {

  @Test
  fun rootDir() {
    assertEquals("vdi/", S3Paths.rootDir())
  }

  @Test
  fun userDir() {
    assertEquals("vdi/b/", S3Paths.userDir(USER_ID))
  }

  @Test
  fun datasetDir() {
    assertEquals("vdi/b/912EC803B2CE49E4A541068D495AB570/", S3Paths.datasetDir(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetManifestFile() {
    assertEquals("vdi/b/912EC803B2CE49E4A541068D495AB570/dataset/manifest.json", S3Paths.datasetManifestFile(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetMetaFile() {
    assertEquals("vdi/b/912EC803B2CE49E4A541068D495AB570/dataset/meta.json", S3Paths.datasetMetaFile(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetDeleteFlagFile() {
    assertEquals("vdi/b/912EC803B2CE49E4A541068D495AB570/dataset/delete-flag", S3Paths.datasetDeleteFlagFile(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetDataDir() {
    assertEquals("vdi/b/912EC803B2CE49E4A541068D495AB570/dataset/data/", S3Paths.datasetDataDir(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetDataFile() {
    assertEquals("vdi/b/912EC803B2CE49E4A541068D495AB570/dataset/data/file.ext", S3Paths.datasetDataFile(USER_ID, DATASET_ID, FILE_NAME))
  }

  @Test
  fun datasetSharesDir() {
    assertEquals("vdi/b/912EC803B2CE49E4A541068D495AB570/dataset/shares/", S3Paths.datasetSharesDir(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetShareOfferFile() {
    assertEquals(
      "vdi/b/912EC803B2CE49E4A541068D495AB570/dataset/shares/d/offer.json",
      S3Paths.datasetShareOfferFile(USER_ID, DATASET_ID, RECIPIENT_ID)
    )
  }

  @Test
  fun datasetShareReceiptFile() {
    assertEquals(
      "vdi/b/912EC803B2CE49E4A541068D495AB570/dataset/shares/d/receipt.json",
      S3Paths.datasetShareReceiptFile(USER_ID, DATASET_ID, RECIPIENT_ID)
    )
  }

  @Test
  fun datasetUploadsDir() {
    assertEquals("vdi/b/912EC803B2CE49E4A541068D495AB570/upload/", S3Paths.datasetUploadsDir(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetUploadFile() {
    assertEquals("vdi/b/912EC803B2CE49E4A541068D495AB570/upload/file.ext", S3Paths.datasetUploadFile(USER_ID, DATASET_ID, FILE_NAME))
  }
}