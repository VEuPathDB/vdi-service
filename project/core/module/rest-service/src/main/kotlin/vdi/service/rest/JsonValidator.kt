package vdi.service.rest

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.readValue
import com.networknt.schema.*
import jakarta.ws.rs.core.HttpHeaders
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.server.Server
import org.veupathdb.lib.request.validation.messageIndex
import vdi.service.rest.gen.annotations.JsonSchema as JsonSchemaAnnotation

class JsonValidator(
  private val schemaPath: String,
  private val jsonMapper: ObjectMapper,
  private val killSwitch: () -> Unit,
) {
  private val logger = LoggerFactory.getLogger(Server::class.java)

  private val schemata = HashMap<Class<*>, JsonSchema>(64)

  private val schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012)!!

  fun loadSchema(model: Class<*>) =
    synchronized(schemaFactory) { schemata.computeIfAbsent(model, ::loadFromFile) }

  fun validate(model: Class<*>, body: JsonNode): Set<ValidationMessage> =
    loadSchema(model).validate(body)

  fun checkInvalid(model: Class<*>, body: JsonNode) =
    validate(model, body)
      .takeUnless(Set<*>::isEmpty)
      ?.build422()

  private fun loadFromFile(model: Class<*>): JsonSchema {
    val name = model.getAnnotation(JsonSchemaAnnotation::class.java).name
    val path = "$schemaPath/${name}.json"

    logger.debug("attempting to load json schema {}", path)

    return when (val stream = model.getResourceAsStream(path)) {
      null -> {
        killSwitch()
        throw RuntimeException("no json schema found for type ${model.name}")
      }
      else -> schemaFactory.getSchema(stream)
    }
  }

  private fun Set<ValidationMessage>.build422(): Response {
    val keyedErrors = HashMap<String, MutableList<String>>(8)
    val furtherProcessing = HashMap<String, MutableList<ValidationMessage>>(8)

    forEach { msg ->
      val path = when (val prop = msg.property) {
        null -> msg.instanceLocation.toString()
        else -> msg.instanceLocation.toString() + ".$prop"
      }

      if (path in furtherProcessing) {
        furtherProcessing[path]!!.add(msg)
        return@forEach
      }

      when (val errorType = ValidatorTypeCode.fromValue(msg.type)) {
        ValidatorTypeCode.ADDITIONAL_PROPERTIES,
        ValidatorTypeCode.UNEVALUATED_PROPERTIES -> {
          // Property may show up as 'unevaluated' for typing errors in some
          // situations.
          if (path !in keyedErrors)
            keyedErrors[path] = mutableListOf("unrecognized additional property")
        }

        ValidatorTypeCode.ANY_OF,
        ValidatorTypeCode.ONE_OF                 -> furtherProcessing.add(path, msg)

        ValidatorTypeCode.ALL_OF                 -> { /* ignore, there are multiple errors for this */ }

        ValidatorTypeCode.CONST                  -> {
          val text = "must be " + (msg.arguments[0] as String)
          if (path in keyedErrors && keyedErrors[path]!![0].startsWith("must be one of ("))
            keyedErrors[path]!![0] = text
          else
            keyedErrors.add(path, text)
        }

        else -> keyedErrors.add(path, errorType.formatErrorMessage(msg))
      }
    }

    furtherProcessing.processFurther(keyedErrors)

    return Response.status(422)
      .entity(mapOf(
        "general" to emptyList<String>(),
        "byKey"   to keyedErrors,
      ))
      .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
      .build()
  }

  private fun <T> MutableMap<String, MutableList<T>>.add(key: String, value: T) {
    computeIfAbsent(key, { ArrayList(2) }).add(value)
  }

  private fun Map<String, List<ValidationMessage>>.processFurther(errors: MutableMap<String, MutableList<String>>) {
    val enum = HashSet<String>(16)
    val misc = HashSet<String>(16)

    forEach { (path, msgs) ->
      msgs.asSequence()
        .drop(1)
        .forEach {
          when (val type = ValidatorTypeCode.fromValue(it.type)) {
            ValidatorTypeCode.ENUM -> jsonMapper.readValue<ArrayNode>(it.arguments[0] as String).mapTo(enum) { j -> j.asText() }

            ValidatorTypeCode.ALL_OF,
            ValidatorTypeCode.ANY_OF,
            ValidatorTypeCode.ONE_OF -> {}

            else -> misc += type.formatErrorMessage(it)
          }
        }

      if (enum.isNotEmpty()) {
        errors.add(path, enum.joinToString(", ", "must be one of (", ")"))
        enum.clear()
      }

      misc.forEach { errors.add(path, it) }
      misc.clear()
    }
  }

  private fun ValidatorTypeCode.formatErrorMessage(msg: ValidationMessage) =
    when(this) {
      //
      // Strings
      //
      ValidatorTypeCode.MAX_LENGTH -> messageIndex.maxLengthErrorMessage(
        msg.arguments[0] as? Int ?: (msg.arguments[0] as String).toInt(),
        msg.instanceNode.textValue().length,
      )
      ValidatorTypeCode.MIN_LENGTH -> messageIndex.minLengthErrorMessage(
        msg.arguments[0] as? Int ?: (msg.arguments[0] as String).toInt(),
        msg.instanceNode.textValue().length,
      )
      // ValidatorTypeCode.FORMAT
      // ValidatorTypeCode.PATTERN


      //
      // Numbers
      //
      ValidatorTypeCode.MINIMUM -> "cannot be less than ${msg.arguments[0]}"
      ValidatorTypeCode.MAXIMUM -> "cannot be greater than ${msg.arguments[0]}"

      ValidatorTypeCode.EXCLUSIVE_MINIMUM -> "must be greater than ${msg.arguments[0]}"
      ValidatorTypeCode.EXCLUSIVE_MAXIMUM -> "must be less than ${msg.arguments[0]}"
      // ValidatorTypeCode.MULTIPLE_OF


      //
      // Arrays
      //
      ValidatorTypeCode.UNIQUE_ITEMS -> "items must be unique"
      ValidatorTypeCode.MIN_ITEMS    -> "must contain at least ${msg.arguments[0]} items"
      ValidatorTypeCode.MAX_ITEMS    -> "cannot contain more than ${msg.arguments[0]} items"
      // ValidatorTypeCode.CONTAINS
      // ValidatorTypeCode.ITEMS
      // ValidatorTypeCode.ITEMS_202012
      // ValidatorTypeCode.MAX_CONTAINS
      // ValidatorTypeCode.MIN_CONTAINS
      // ValidatorTypeCode.PREFIX_ITEMS
      // ValidatorTypeCode.UNEVALUATED_ITEMS


      //
      // Objects
      //
      ValidatorTypeCode.ADDITIONAL_PROPERTIES,
      ValidatorTypeCode.UNEVALUATED_PROPERTIES -> "unrecognized additional property"

      ValidatorTypeCode.REQUIRED -> messageIndex.nullErrorMessage
      // ValidatorTypeCode.MIN_PROPERTIES
      // ValidatorTypeCode.MAX_PROPERTIES
      // ValidatorTypeCode.PATTERN_PROPERTIES
      // ValidatorTypeCode.PROPERTYNAMES
      // ValidatorTypeCode.PROPERTIES

      //
      // Misc
      //
      ValidatorTypeCode.TYPE -> when(msg.arguments[0]) {
        "null" -> messageIndex.nullErrorMessage
        else   -> if (msg.arguments.size == 2) {
          "expected type ${msg.arguments[1]}"
        } else {
          msg.arguments.asSequence().drop(1).joinToString(", ", "expected one of the types (", ")")
        }
      }

      ValidatorTypeCode.ENUM -> jsonMapper.readValue<ArrayNode>(msg.arguments[0] as String)
        .joinToString(", ", "must be one of (", ")")

      ValidatorTypeCode.CONST -> "must be " + (msg.arguments[0] as String)

      // ValidatorTypeCode.ALL_OF
      // ValidatorTypeCode.ANY_OF
      // ValidatorTypeCode.ONE_OF
      // ValidatorTypeCode.NOT_ALLOWED
      // ValidatorTypeCode.DEPENDENCIES
      // ValidatorTypeCode.DEPENDENT_REQUIRED
      // ValidatorTypeCode.DEPENDENT_SCHEMAS
      // ValidatorTypeCode.DISCRIMINATOR
      // ValidatorTypeCode.DYNAMIC_REF
      // ValidatorTypeCode.IF_THEN_ELSE
      // ValidatorTypeCode.NOT
      // ValidatorTypeCode.UNION_TYPE
      // ValidatorTypeCode.CONTENT_ENCODING
      // ValidatorTypeCode.CONTENT_MEDIA_TYPE
      // ValidatorTypeCode.FALSE
      // ValidatorTypeCode.ID
      // ValidatorTypeCode.READ_ONLY
      // ValidatorTypeCode.RECURSIVE_REF
      // ValidatorTypeCode.REF
      // ValidatorTypeCode.TRUE
      // ValidatorTypeCode.WRITE_ONLY
      else -> msg.message.substringAfter(": ")
    }
}