package vdi.util

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule

val JSON = JsonMapper.builder()
  .addModule(ParameterNamesModule())
  .addModule(Jdk8Module())
  .addModule(JavaTimeModule())
  .addModule(KotlinModule.Builder().build())
  .build()

@Suppress("NOTHING_TO_INLINE")
inline fun Any.toJSONString() = JSON.writeValueAsString(this)!!