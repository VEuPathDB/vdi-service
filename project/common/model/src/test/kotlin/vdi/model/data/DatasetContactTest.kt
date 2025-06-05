package vdi.model.data

import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.*
import vdi.json.JSON
import vdi.json.toJSONString

@DisplayName("DatasetContact")
class DatasetContactTest {
  @Nested
  @DisplayName("Deserialization")
  inner class Deserialization {
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

    @Test
    fun test6() {
      val result = JSON.readValue<DatasetContact>("""
        {
          "name": "foobar",
          "email": "email@email.email",
          "affiliation": "something",
          "city": "nowhere",
          "state": "guam",
          "country": "alabama"
        }
      """.trimIndent())

      assertEquals(result.name, "foobar")
      assertEquals(result.email, "email@email.email")
      assertEquals(result.affiliation, "something")
      assertEquals(result.city, "nowhere")
      assertEquals(result.state, "guam")
      assertEquals(result.country, "alabama")
      assertNull(result.address)
      assertFalse(result.isPrimary)
    }

    @Test
    fun test7() {
      val result = JSON.readValue<DatasetContact>("""
        {
          "name": "foobar",
          "email": "email@email.email",
          "affiliation": "something",
          "city": "nowhere",
          "state": "guam",
          "country": "alabama",
          "address": "extinction"
        }
      """.trimIndent())

      assertEquals(result.name, "foobar")
      assertEquals(result.email, "email@email.email")
      assertEquals(result.affiliation, "something")
      assertEquals(result.city, "nowhere")
      assertEquals(result.state, "guam")
      assertEquals(result.country, "alabama")
      assertEquals(result.address, "extinction")
      assertFalse(result.isPrimary)
    }

    @Test
    fun test8() {
      val result = JSON.readValue<DatasetContact>("""
        {
          "name": "foobar",
          "email": "email@email.email",
          "affiliation": "something",
          "city": "nowhere",
          "state": "guam",
          "country": "alabama",
          "address": "extinction",
          "isPrimary": true
        }
      """.trimIndent())

      assertEquals(result.name, "foobar")
      assertEquals(result.email, "email@email.email")
      assertEquals(result.affiliation, "something")
      assertEquals(result.city, "nowhere")
      assertEquals(result.state, "guam")
      assertEquals(result.country, "alabama")
      assertEquals(result.address, "extinction")
      assertTrue(result.isPrimary)
    }
  }

  @Nested
  @DisplayName("Serialization")
  inner class Serialization {
    @Test
    fun test1() {
      assertEquals(DatasetContact("foobar").toJSONString(), """{"name":"foobar"}""")
    }

    @Test
    fun test2() {
      assertEquals(DatasetContact(
        name  = "foobar",
        email = "email@email.email",
      ).toJSONString(), """{"name":"foobar","email":"email@email.email"}""")
    }

    @Test
    fun test3() {
      assertEquals(DatasetContact(
        name        = "foobar",
        email       = "email@email.email",
        affiliation = "something",
      ).toJSONString(), """{"name":"foobar","email":"email@email.email","affiliation":"something"}""")
    }

    @Test
    fun test4() {
      assertEquals(DatasetContact(
        name        = "foobar",
        email       = "email@email.email",
        affiliation = "something",
        city        = "nowhere",
      ).toJSONString(), """{"name":"foobar","email":"email@email.email","affiliation":"something","city":"nowhere"}""")
    }

    @Test
    fun test5() {
      assertEquals(DatasetContact(
        name        = "foobar",
        email       = "email@email.email",
        affiliation = "something",
        city        = "nowhere",
        state       = "guam",
      ).toJSONString(), """{"name":"foobar","email":"email@email.email","affiliation":"something","city":"nowhere","state":"guam"}""")
    }

    @Test
    fun test6() {
      assertEquals(DatasetContact(
        name        = "foobar",
        email       = "email@email.email",
        affiliation = "something",
        city        = "nowhere",
        state       = "guam",
        country     = "alabama",
      ).toJSONString(), """{"name":"foobar","email":"email@email.email","affiliation":"something","city":"nowhere","state":"guam","country":"alabama"}""")
    }

    @Test
    fun test7() {
      assertEquals(DatasetContact(
        name        = "foobar",
        email       = "email@email.email",
        affiliation = "something",
        city        = "nowhere",
        state       = "guam",
        country     = "alabama",
        address     = "extinction",
      ).toJSONString(), """{"name":"foobar","email":"email@email.email","affiliation":"something","city":"nowhere","state":"guam","country":"alabama","address":"extinction"}""")
    }

    @Test
    fun test8() {
      assertEquals(DatasetContact(
        name        = "foobar",
        email       = "email@email.email",
        affiliation = "something",
        city        = "nowhere",
        state       = "guam",
        country     = "alabama",
        address     = "extinction",
        isPrimary   = true,
      ).toJSONString(), """{"name":"foobar","email":"email@email.email","affiliation":"something","city":"nowhere","state":"guam","country":"alabama","address":"extinction","isPrimary":true}""")
    }
  }
}