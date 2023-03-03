package vdi.components.datasets.paths

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

private const val USER_ID = "b"
private const val DATASET_ID = "c"
private const val RECIPIENT_ID = "d"
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
    assertEquals("vdi/b/c/", S3Paths.datasetDir(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetManifestFile() {
    assertEquals("vdi/b/c/dataset/manifest.json", S3Paths.datasetManifestFile(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetMetaFile() {
    assertEquals("vdi/b/c/dataset/meta.json", S3Paths.datasetMetaFile(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetDeleteFlagFile() {
    assertEquals("vdi/b/c/dataset/delete-flag", S3Paths.datasetDeleteFlagFile(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetDataDir() {
    assertEquals("vdi/b/c/dataset/data/", S3Paths.datasetDataDir(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetDataFile() {
    assertEquals("vdi/b/c/dataset/data/file.ext", S3Paths.datasetDataFile(USER_ID, DATASET_ID, FILE_NAME))
  }

  @Test
  fun datasetSharesDir() {
    assertEquals("vdi/b/c/dataset/shares/", S3Paths.datasetSharesDir(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetShareOfferFile() {
    assertEquals(
      "vdi/b/c/dataset/shares/d/offer.json",
      S3Paths.datasetShareOfferFile(USER_ID, DATASET_ID, RECIPIENT_ID)
    )
  }

  @Test
  fun datasetShareReceiptFile() {
    assertEquals(
      "vdi/b/c/dataset/shares/d/receipt.json",
      S3Paths.datasetShareReceiptFile(USER_ID, DATASET_ID, RECIPIENT_ID)
    )
  }

  @Test
  fun datasetUploadsDir() {
    assertEquals("vdi/b/c/upload/", S3Paths.datasetUploadsDir(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetUploadFile() {
    assertEquals("vdi/b/c/upload/file.ext", S3Paths.datasetUploadFile(USER_ID, DATASET_ID, FILE_NAME))
  }
}