package vdi.model.data

import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import vdi.json.JSON
import vdi.json.toJSONString

@DisplayName("DatasetDependency")
class DatasetDependencyTest {
  @Nested
  inner class Deserialization {
    @Test
    fun test1() {
      val result = JSON.readValue<DatasetDependency>("""
        {
          "resourceIdentifier": "something",
          "resourceVersion": "numbers",
          "resourceDisplayName": "fancy words"
        }
      """.trimIndent())

      assertEquals("something", result.identifier)
      assertEquals("numbers", result.version)
      assertEquals("fancy words", result.displayName)
    }

  }

  @Nested
  inner class Serialization {
    @Test
    fun test1() {
      assertEquals(DatasetDependency(
        identifier  = "something",
        version     = "numbers",
        displayName = "fancy words",
      ).toJSONString(), """{"resourceIdentifier":"something","resourceVersion":"numbers","resourceDisplayName":"fancy words"}""")
    }
  }
}