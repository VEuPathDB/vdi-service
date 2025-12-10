package vdi.model.meta

import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import vdi.json.JSON
import vdi.json.toJSONString

@DisplayName("DatasetFileInfo")
class DatasetFileInfoTest {
  @Nested
  @DisplayName("Deserialize")
  inner class Deserialize {
    @Test
    @DisplayName("current field names")
    fun test1() {
      JSON.readValue<DatasetFileInfo>(
        // language=json
        """
        {
          "name": "file",
          "size": 234
        }
        """
      ).also {
        assertEquals("file", it.name)
        assertEquals(234uL, it.size)
      }
    }

    @Test
    @DisplayName("legacy field names")
    fun test2() {
      JSON.readValue<DatasetFileInfo>(
        // language=json
        """
        {
          "filename": "file",
          "fileSize": 234
        }
        """
      ).also {
        assertEquals("file", it.name)
        assertEquals(234uL, it.size)
      }
    }
  }

  @Nested
  @DisplayName("Serialize")
  inner class Serialize {
    @Test
    fun test1() {
      assertEquals("""{"name":"file","size":234}""", DatasetFileInfo("file", 234uL).toJSONString())
    }
  }
}