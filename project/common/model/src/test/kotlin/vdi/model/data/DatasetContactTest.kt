package vdi.model.data

import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import vdi.json.JSON

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatasetContactTest {
  @Nested
  @DisplayName("Deserialization")
  class Deserialization {
    @Test
    fun test1() {
      val result = JSON.readValue<DatasetContact>("""
        {
          "name": "foobar"
        }
      """.trimIndent())

      assertEquals(result.name, "foobar")
      assertNull(result.email)
      assertNull(result.affiliation)
      assertNull(result.city)
      assertNull(result.state)
      assertNull(result.country)
      assertNull(result.address)
      assertFalse(result.isPrimary)
    }

    @Test
    fun test2() {
      val result = JSON.readValue<DatasetContact>("""
        {
          "name": "foobar",
          "email": "email@email.email"
        }
      """.trimIndent())

      assertEquals(result.name, "foobar")
      assertEquals(result.email, "email@email.email")
      assertNull(result.affiliation)
      assertNull(result.city)
      assertNull(result.state)
      assertNull(result.country)
      assertNull(result.address)
      assertFalse(result.isPrimary)
    }

    @Test
    fun test3() {
      val result = JSON.readValue<DatasetContact>("""
        {
          "name": "foobar",
          "email": "email@email.email",
          "affiliation": "something"
        }
      """.trimIndent())

      assertEquals(result.name, "foobar")
      assertEquals(result.email, "email@email.email")
      assertEquals(result.affiliation, "something")
      assertNull(result.city)
      assertNull(result.state)
      assertNull(result.country)
      assertNull(result.address)
      assertFalse(result.isPrimary)
    }

    @Test
    fun test4() {
      val result = JSON.readValue<DatasetContact>("""
        {
          "name": "foobar",
          "email": "email@email.email",
          "affiliation": "something",
          "city": "nowhere"
        }
      """.trimIndent())

      assertEquals(result.name, "foobar")
      assertEquals(result.email, "email@email.email")
      assertEquals(result.affiliation, "something")
      assertEquals(result.city, "nowhere")
      assertNull(result.state)
      assertNull(result.country)
      assertNull(result.address)
      assertFalse(result.isPrimary)
    }

    @Test
    fun test5() {
      val result = JSON.readValue<DatasetContact>("""
        {
          "name": "foobar",
          "email": "email@email.email",
          "affiliation": "something",
          "city": "nowhere",
          "state": "guam"
        }
      """.trimIndent())

      assertEquals(result.name, "foobar")
      assertEquals(result.email, "email@email.email")
      assertEquals(result.affiliation, "something")
      assertEquals(result.city, "nowhere")
      assertEquals(result.state, "guam")
      assertNull(result.country)
      assertNull(result.address)
      assertFalse(result.isPrimary)
    }
  }
}