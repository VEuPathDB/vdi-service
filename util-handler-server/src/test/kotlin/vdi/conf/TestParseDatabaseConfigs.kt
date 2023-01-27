package vdi.conf

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("parseDatabaseConfigs(MutableMap<String, String>)")
class TestParseDatabaseConfigs {

  @Test
  @DisplayName("Success Case")
  fun t1() {
    val input = mutableMapOf(
      "DB_CONNECTION_NAME_PLASMO_DB" to "PlasmoDB",
      "DB_CONNECTION_LDAP_PLASMO_DB" to "foo",
      "DB_CONNECTION_USER_PLASMO_DB" to "bar",
      "DB_CONNECTION_PASS_PLASMO_DB" to "fizz",

      "DB_CONNECTION_NAME_TOXO_DB" to "ToxoDB",
      "DB_CONNECTION_LDAP_TOXO_DB" to "buzz",
      "DB_CONNECTION_USER_TOXO_DB" to "ding",
      "DB_CONNECTION_PASS_TOXO_DB" to "dong",
    )

    val output = parseDatabaseConfigs(input)

    assertEquals(2, output.size)
    assertTrue("PlasmoDB" in output)
    assertTrue("ToxoDB" in output)

    assertEquals("PlasmoDB", output["PlasmoDB"]!!.name)
    assertEquals("foo", output["PlasmoDB"]!!.ldap.value)
    assertEquals("bar", output["PlasmoDB"]!!.user.value)
    assertEquals("fizz", output["PlasmoDB"]!!.pass.value)

    assertEquals("ToxoDB", output["ToxoDB"]!!.name)
    assertEquals("buzz", output["ToxoDB"]!!.ldap.value)
    assertEquals("ding", output["ToxoDB"]!!.user.value)
    assertEquals("dong", output["ToxoDB"]!!.pass.value)
  }

  @Test
  @DisplayName("Fails when group name is missing")
  fun t2() {
    val input = mutableMapOf(
      "DB_CONNECTION_LDAP_PLASMO_DB" to "foo",
      "DB_CONNECTION_USER_PLASMO_DB" to "bar",
      "DB_CONNECTION_PASS_PLASMO_DB" to "fizz",
    )

    assertThrows<RuntimeException> { parseDatabaseConfigs(input) }
  }

  @Test
  @DisplayName("Fails when group ldap is missing")
  fun t3() {
    val input = mutableMapOf(
      "DB_CONNECTION_NAME_PLASMO_DB" to "PlasmoDB",
      "DB_CONNECTION_USER_PLASMO_DB" to "bar",
      "DB_CONNECTION_PASS_PLASMO_DB" to "fizz",
    )

    assertThrows<RuntimeException> { parseDatabaseConfigs(input) }
  }

  @Test
  @DisplayName("Fails when group user name is missing")
  fun t4() {
    val input = mutableMapOf(
      "DB_CONNECTION_NAME_PLASMO_DB" to "PlasmoDB",
      "DB_CONNECTION_LDAP_PLASMO_DB" to "foo",
      "DB_CONNECTION_PASS_PLASMO_DB" to "fizz",
    )

    assertThrows<RuntimeException> { parseDatabaseConfigs(input) }
  }

  @Test
  @DisplayName("Fails when group password is missing")
  fun t5() {
    val input = mutableMapOf(
      "DB_CONNECTION_NAME_PLASMO_DB" to "PlasmoDB",
      "DB_CONNECTION_LDAP_PLASMO_DB" to "foo",
      "DB_CONNECTION_USER_PLASMO_DB" to "bar",
    )

    assertThrows<RuntimeException> { parseDatabaseConfigs(input) }
  }

  @Test
  @DisplayName("Fails when no db groups are present")
  fun t6() {
    val input = mutableMapOf<String, String>()

    assertThrows<RuntimeException> { parseDatabaseConfigs(input) }
  }

}