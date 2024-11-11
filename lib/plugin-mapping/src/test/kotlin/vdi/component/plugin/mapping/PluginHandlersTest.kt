package vdi.component.plugin.mapping

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.veupathdb.vdi.lib.common.field.DataType

@DisplayName("PluginHandlers")
class PluginHandlersTest {

  @Nested
  @DisplayName("init")
  inner class Init {

    @Test
    @DisplayName("init with partial env block throws an exception (1)")
    fun `init-with-partial-env-block-throws-an-exception-1`() {
      // Construct a map that only has a name (missing project list and address)
      val env1 = mapOf("PLUGIN_HANDLER_FOO_NAME" to "foo")

      assertThrows<IllegalStateException> { PluginHandlers.init(env1) }
    }

    @Test
    @DisplayName("init with partial env block throws an exception (2)")
    fun `init-with-partial-env-block-throws-an-exception-2`() {
      // Construct a map that only has a name and project list (missing address)
      val env3 = mapOf(
        "PLUGIN_HANDLER_FIZZ_NAME"        to "fizz",
        "PLUGIN_HANDLER_FIZZ_PROJECT_IDS" to ""
      )

      assertThrows<IllegalStateException> { PluginHandlers.init(env3) }
    }

    @Test
    @DisplayName("registers a handler for a complete block")
    fun `registers-a-handler-for-a-complete-block`() {
      // Construct a map with a complete environment block for a handler config
      val env2 = mapOf(
        "PLUGIN_HANDLER_BAR_NAME"        to "bar",
        "PLUGIN_HANDLER_BAR_VERSION"     to "1.0",
        "PLUGIN_HANDLER_BAR_ADDRESS"     to "foo:1234",
        "PLUGIN_HANDLER_BAR_PROJECT_IDS" to "project1,project2,project3"
      )

      PluginHandlers.init(env2)

      assertTrue(PluginHandlers.contains(DataType.of("Bar"), "1.0"))
    }
  }
}
