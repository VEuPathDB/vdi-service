package vdi.model.meta

import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import vdi.json.JSON
import vdi.json.toJSONString

@DisplayName("DatasetManifest")
class DatasetManifestTest {
  @Nested
  @DisplayName("Deserialization")
  inner class Deserialization {
    @Test
    @DisplayName("current field names")
    fun test1() {

      JSON.readValue<DatasetManifest>(
        // language=json
        """
        {
          "rawUploads": [
            {"name": "file", "size": 1234 }
          ],
          "installReady": [
            {"name": "file1", "size": 1234 },
            {"name": "file2", "size": 3456 }
          ] 
        }
        """
      ).also {
        assertEquals(1, it.userUploadFiles.size)
        with (it.userUploadFiles.first()) {
          assertEquals("file", name)
          assertEquals(1234uL, size)
        }

        assertEquals(2, it.installReadyFiles.size)
        with (it.installReadyFiles.first()) {
          assertEquals("file1", name)
          assertEquals(1234uL, size)
        }
        with (it.installReadyFiles.elementAt(1)) {
          assertEquals("file2", name)
          assertEquals(3456uL, size)
        }
      }
    }

    @Test
    @DisplayName("legacy field names")
    fun test2() {
      JSON.readValue<DatasetManifest>(
        // language=json
        """
        {
          "inputFiles": [ {"name": "file", "size": 1234 } ],
          "dataFiles": [
            {"name": "file1", "size": 1234 },
            {"name": "file2", "size": 3456 }
          ] 
        }
        """
      ).also {
        assertEquals(1, it.userUploadFiles.size)
        with (it.userUploadFiles.first()) {
          assertEquals("file", name)
          assertEquals(1234uL, size)
        }

        assertEquals(2, it.installReadyFiles.size)
        with (it.installReadyFiles.first()) {
          assertEquals("file1", name)
          assertEquals(1234uL, size)
        }
        with (it.installReadyFiles.elementAt(1)) {
          assertEquals("file2", name)
          assertEquals(3456uL, size)
        }
      }
    }

    @Test
    @DisplayName("minimal representation")
    fun test3() {
      JSON.readValue<DatasetManifest>(
        // language=json
        """
        {
          "rawUploads": [ {"name": "file", "size": 1234 } ]
        }
        """
      ).also {
        assertEquals(1, it.userUploadFiles.size)
        with (it.userUploadFiles.first()) {
          assertEquals("file", name)
          assertEquals(1234uL, size)
        }

        assertEquals(0, it.installReadyFiles.size)
      }
    }
  }

  @Nested
  @DisplayName("Serialization")
  inner class Serialization {
    @Test
    @DisplayName("minimal representation")
    fun test1() {
      assertEquals(
        // language=json
        """{"rawUploads":[{"name":"file","size":1234}]}""",
        DatasetManifest(listOf(DatasetFileInfo("file", 1234uL))).toJSONString(),
      )
    }

    @Test
    @DisplayName("full representation")
    fun test2() {
      assertEquals(
        // language=json
        """{"rawUploads":[{"name":"file","size":1234}],"installReady":[{"name":"file1","size":1234},{"name":"file2","size":3456}]}""",
        DatasetManifest(
          listOf(DatasetFileInfo("file", 1234uL)),
          listOf(
            DatasetFileInfo("file1", 1234u),
            DatasetFileInfo("file2", 3456u),
          )
        ).toJSONString(),
      )
    }
  }
}