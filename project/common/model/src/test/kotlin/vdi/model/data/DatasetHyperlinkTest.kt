package vdi.model.data

import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import java.net.URI
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import vdi.json.JSON
import vdi.json.toJSONString

@DisplayName("DatasetHyperlink")
class DatasetHyperlinkTest {
  @Nested
  @DisplayName("Deserialization")
  inner class Deserialization {
    @Test
    @DisplayName("minimal representation")
    fun test1() {
      JSON.readValue<DatasetHyperlink>("""
        {
          "url": "some.site.com",
          "text": "My hyperlink"
        }
      """.trimIndent()).also {
        assertEquals(URI.create("some.site.com"), it.url)
        assertEquals("My hyperlink", it.text)
        assertNull(it.description)
        assertFalse(it.isPublication)
      }
    }

    @Test
    @DisplayName("with description")
    fun test2() {
      JSON.readValue<DatasetHyperlink>("""
        {
          "url": "some.site.com",
          "text": "My hyperlink",
          "description": "something"
        }
      """.trimIndent()).also {
        assertEquals(URI.create("some.site.com"), it.url)
        assertEquals("My hyperlink", it.text)
        assertEquals("something", it.description)
        assertFalse(it.isPublication)
      }
    }

    @Test
    @DisplayName("with isPublication")
    fun test3() {
      JSON.readValue<DatasetHyperlink>("""
        {
          "url": "some.site.com",
          "text": "My hyperlink",
          "description": "something",
          "isPublication": true
        }
      """.trimIndent()).also {
        assertEquals(URI.create("some.site.com"), it.url)
        assertEquals("My hyperlink", it.text)
        assertEquals("something", it.description)
        assertTrue(it.isPublication)
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
        """{"url":"some.site.com","text":"My hyperlink"}""",
        DatasetHyperlink(URI.create("some.site.com"), "My hyperlink").toJSONString()
      )
    }

    @Test
    @DisplayName("with description")
    fun test2() {
      assertEquals(
        """{"url":"some.site.com","text":"My hyperlink","description":"something"}""",
        DatasetHyperlink(URI.create("some.site.com"), "My hyperlink", "something").toJSONString()
      )
    }

    @Test
    @DisplayName("with isPublication")
    fun test3() {
      assertEquals(
        """{"url":"some.site.com","text":"My hyperlink","description":"something","isPublication":true}""",
        DatasetHyperlink(URI.create("some.site.com"), "My hyperlink", "something", true).toJSONString()
      )
    }
  }
}