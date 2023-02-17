package vdi.components.common

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import vdi.components.common.DatasetID

@DisplayName("DatasetID")
class DatasetIDTest {

  @Nested
  @DisplayName("constructor")
  inner class Constructor {

    @Nested
    @DisplayName("throws an IllegalArgumentException when")
    inner class ThrowsIllegalArgumentException {

      @Test
      @DisplayName("the input string is not 32 characters in length")
      fun t1() {
        val tests = arrayOf(
          "", "0",
          "0000000000000000000000000000000",  // 31 chars
          "000000000000000000000000000000000" // 33 chars
        )

        for (test in tests)
          assertThrows<IllegalArgumentException> { DatasetID(test) }
      }

      @Test
      @DisplayName("the input string contains non-hex characters")
      fun t2() {
        val tests = arrayOf("0000000000000000000000000000000G")

        for (test in tests)
          assertThrows<IllegalArgumentException> { DatasetID(test) }
      }
    }
  }
}