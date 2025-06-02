package vdi.json

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import java.text.SimpleDateFormat
import java.util.TimeZone

/**
 * Jackson Object Mapper
 *
 * Preconfigured Jackson JSON factory instance that may be used to construct,
 * parse, or produce JSON values.
 *
 * @since 1.0.0
 */
val JSON = JsonMapper.builder()
  .addModule(ParameterNamesModule())
  .addModule(Jdk8Module())
  .addModule(JavaTimeModule())
  .addModule(KotlinModule.Builder().build())
  .build()!!
  .also {
    it.dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
    it.dateFormat.timeZone = TimeZone.getDefault()
  }

/**
 * Writes an arbitrary value as a JSON string.
 *
 * @receiver Object to write as a JSON string.
 *
 * @return A JSON string representation of the target object.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Any.toJSONString() = JSON.writeValueAsString(this)!!