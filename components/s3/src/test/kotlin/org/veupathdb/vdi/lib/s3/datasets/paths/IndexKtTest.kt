package org.veupathdb.vdi.lib.s3.datasets.paths

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.assertIs

@DisplayName("index.kt")
class IndexKtTest {

  @Nested
  @DisplayName("String.toVDPathOrNull")
  inner class ToVDPathOrNull {

    @Test
    @DisplayName("parses upload paths")
    fun t1() {
      val path = "bucket/vdi/111/aa41efe0a1b3eeb9bf303e4561ff8392/upload/foo.txt".toVDPathOrNull()

      assertNotNull(path)
      assertIs<VDUploadPath>(path)
      assertEquals("bucket", path.bucketName)
      assertEquals("vdi", path.rootSegment)
      assertEquals("111", path.userID.toString())
      assertEquals("aa41efe0a1b3eeb9bf303e4561ff8392", path.datasetID.toString())
      assertEquals("foo.txt", path.subPath)
    }

    @Test
    @DisplayName("parses dataset root paths")
    fun t2() {
      val path = "bucket/vdi/111/aa41efe0a1b3eeb9bf303e4561ff8392/dataset/foo.txt".toVDPathOrNull()

      assertNotNull(path)
      assertIs<VDDatasetFilePath>(path)
      assertEquals("bucket", path.bucketName)
      assertEquals("vdi", path.rootSegment)
      assertEquals("111", path.userID.toString())
      assertEquals("aa41efe0a1b3eeb9bf303e4561ff8392", path.datasetID.toString())
      assertEquals("foo.txt", path.subPath)
    }

    @Test
    @DisplayName("parses dataset share file paths")
    fun t3() {
      val path = "bucket/vdi/111/aa41efe0a1b3eeb9bf303e4561ff8392/dataset/shares/222/offer.json".toVDPathOrNull()

      assertNotNull(path)
      assertIs<VDDatasetShareFilePath>(path)
      assertEquals("bucket", path.bucketName)
      assertEquals("vdi", path.rootSegment)
      assertEquals("111", path.userID.toString())
      assertEquals("aa41efe0a1b3eeb9bf303e4561ff8392", path.datasetID.toString())
      assertEquals("222", path.recipientID.toString())
      assertEquals("offer.json", path.subPath)
    }

    @Test
    @DisplayName("parses dataset data file paths")
    fun t4() {
      val path = "bucket/vdi/111/aa41efe0a1b3eeb9bf303e4561ff8392/dataset/data/butt.xml".toVDPathOrNull()

      assertNotNull(path)
      assertIs<VDDatasetDataFilePath>(path)
      assertEquals("bucket", path.bucketName)
      assertEquals("vdi", path.rootSegment)
      assertEquals("111", path.userID.toString())
      assertEquals("aa41efe0a1b3eeb9bf303e4561ff8392", path.datasetID.toString())
      assertEquals("butt.xml", path.subPath)
    }
  }

}