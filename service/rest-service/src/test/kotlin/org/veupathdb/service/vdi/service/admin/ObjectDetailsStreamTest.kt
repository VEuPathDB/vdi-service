package org.veupathdb.service.vdi.service.admin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.service.vdi.util.DateFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*
import kotlin.random.Random

class ObjectDetailsStreamTest {
  @Test
  fun test1() {
    val dates = Array(100) {
      OffsetDateTime.ofInstant(Instant.ofEpochMilli(Random.nextLong(590558400000, 3746318400000)), ZoneId.systemDefault())
    }

    val objects = Array(100) {
      mockS3(UUID.randomUUID().toString(), Random.nextLong(0, Long.MAX_VALUE), dates[it])
    }

    val stream = ObjectDetailsStream(Arrays.stream(objects))
      .bufferedReader()

    for (obj in objects)
      assertEquals(obj.testString(), stream.readLine())

    assertNull(stream.readLine())
  }

  private fun S3Object.testString() = "$path\t$size\t${lastModified!!.format(DateFormat)}"

  private fun mockS3(path: String, size: Long, date: OffsetDateTime): S3Object = mock {
    on { this.path } doReturn path
    on { this.size } doReturn size
    on { this.lastModified } doReturn date
  }
}