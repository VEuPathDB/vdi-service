package org.veupathdb.vdi.lib.s3.datasets.paths

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.veupathdb.vdi.lib.common.DatasetManifestFilename
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

private val USER_ID = UserID("1")
private val DATASET_ID = DatasetID("912ec803b2ce49e4a541068d495ab570")
private val RECIPIENT_ID = UserID("2")
private const val FILE_NAME = "file.ext"


@DisplayName("S3Paths")
class S3PathsTest {

  @Test
  fun rootDir() {
    assertEquals("vdi/", S3Paths.rootDir())
  }

  @Test
  fun userDir() {
    assertEquals("vdi/1/", S3Paths.userDir(USER_ID))
  }

  @Test
  fun datasetDir() {
    assertEquals("vdi/1/912ec803b2ce49e4a541068d495ab570/", S3Paths.datasetDir(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetManifestFile() {
    assertEquals("vdi/1/912ec803b2ce49e4a541068d495ab570/dataset/$DatasetManifestFilename", S3Paths.datasetManifestFile(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetMetaFile() {
    assertEquals("vdi/1/912ec803b2ce49e4a541068d495ab570/dataset/$DatasetMetaFilename", S3Paths.datasetMetaFile(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetDeleteFlagFile() {
    assertEquals("vdi/1/912ec803b2ce49e4a541068d495ab570/dataset/delete-flag", S3Paths.datasetDeleteFlagFile(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetDataDir() {
    assertEquals("vdi/1/912ec803b2ce49e4a541068d495ab570/dataset/data/", S3Paths.datasetDataDir(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetDataFile() {
    assertEquals("vdi/1/912ec803b2ce49e4a541068d495ab570/dataset/data/file.ext", S3Paths.datasetDataFile(USER_ID, DATASET_ID, FILE_NAME))
  }

  @Test
  fun datasetSharesDir() {
    assertEquals("vdi/1/912ec803b2ce49e4a541068d495ab570/dataset/shares/", S3Paths.datasetSharesDir(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetShareOfferFile() {
    assertEquals(
      "vdi/1/912ec803b2ce49e4a541068d495ab570/dataset/shares/2/offer.json",
      S3Paths.datasetShareOfferFile(USER_ID, DATASET_ID, RECIPIENT_ID)
    )
  }

  @Test
  fun datasetShareReceiptFile() {
    assertEquals(
      "vdi/1/912ec803b2ce49e4a541068d495ab570/dataset/shares/2/receipt.json",
      S3Paths.datasetShareReceiptFile(USER_ID, DATASET_ID, RECIPIENT_ID)
    )
  }

  @Test
  fun datasetUploadsDir() {
    assertEquals("vdi/1/912ec803b2ce49e4a541068d495ab570/upload/", S3Paths.datasetUploadsDir(USER_ID, DATASET_ID))
  }

  @Test
  fun datasetUploadFile() {
    assertEquals("vdi/1/912ec803b2ce49e4a541068d495ab570/upload/file.ext", S3Paths.datasetUploadFile(USER_ID, DATASET_ID, FILE_NAME))
  }
}