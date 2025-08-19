@file:JvmName("JsonSchemaValidator")
package vdi.service.rest.server.inputs

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import com.networknt.schema.ExecutionContext
import com.networknt.schema.JsonSchema
import com.networknt.schema.ValidationMessage
import com.networknt.schema.ValidatorTypeCode
import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.messageIndex
import org.veupathdb.lib.request.validation.rangeTo
import vdi.json.JSON

fun ObjectNode.validate(schema: JsonSchema, jPath: String, errors: ValidationErrors) {
  val result = schema.validate(this) { ctx: ExecutionContext -> ctx.executionConfig.formatAssertionsEnabled = true }

  if (result.isEmpty())
    return

  val furtherProcessing = HashMap<String, MutableList<ValidationMessage>>(8)

  result.forEach { msg ->
    val path = when (val loc = msg.instanceLocation.toString()) {
      "$"  -> jPath
      else -> jPath..loc.substring(2)
    }.let {
      if (msg.property != null)
        it..msg.property
      else
        it
    }

    if (path in furtherProcessing) {
      furtherProcessing[path]!!.add(msg)
      return@forEach // continue
    }

    when (val type = ValidatorTypeCode.fromValue(msg.type)) {
      ValidatorTypeCode.ADDITIONAL_PROPERTIES,
      ValidatorTypeCode.UNEVALUATED_PROPERTIES -> {
        // Property may show up as 'unevaluated' for typing errors in some
        // situations.
        if (path !in errors.byKey)
          errors.add(path, "unrecognized additional property")
      }

      ValidatorTypeCode.ANY_OF,
      ValidatorTypeCode.ONE_OF                 -> furtherProcessing.computeIfAbsent(path) { ArrayList(4) }
        .add(msg)
      ValidatorTypeCode.ALL_OF                 -> { /* ignore, there will be other errors for this */ }
      ValidatorTypeCode.CONST                  -> {
        val text = "must be " + (msg.arguments[0] as String)
        if (path in errors.byKey && errors.byKey[path]!![0].startsWith("must be one of ("))
          errors.byKey[path]!![0] = text
        else
          errors.add(path, text)
      }

      else -> errors.add(path, type.formatErrorMessage(msg))
    }
  }

  furtherProcessing.processFurther(errors)
}

private fun Map<String, List<ValidationMessage>>.processFurther(errors: ValidationErrors) {
  val enum = HashSet<String>(16)
  val misc = HashSet<String>(16)

  forEach { (path, msgs) ->
    msgs.asSequence()
      .drop(1)
      .forEach {
        when (val type = ValidatorTypeCode.fromValue(it.type)) {
          ValidatorTypeCode.ENUM  -> JSON.readValue<ArrayNode>(it.arguments[0] as String).mapTo(enum) { j -> j.asText() }

          ValidatorTypeCode.ALL_OF,
          ValidatorTypeCode.ANY_OF,
          ValidatorTypeCode.ONE_OF-> {}

          else                    -> misc += type.formatErrorMessage(it)
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

private fun ValidatorTypeCode.formatErrorMessage(msg: ValidationMessage): String {
  return when(this) {
    //
    // Strings
    //
    ValidatorTypeCode.MAX_LENGTH            -> messageIndex.maxLengthErrorMessage(
      msg.arguments[0] as? Int ?: (msg.arguments[0] as String).toInt(),
      msg.instanceNode.textValue().length,
    )
    ValidatorTypeCode.MIN_LENGTH            -> messageIndex.minLengthErrorMessage(
      msg.arguments[0] as? Int ?: (msg.arguments[0] as String).toInt(),
      msg.instanceNode.textValue().length,
    )
    // ValidatorTypeCode.FORMAT
    // ValidatorTypeCode.PATTERN


    //
    // Numbers
    //
    ValidatorTypeCode.MINIMUM               -> "cannot be less than ${msg.arguments[0]}"
    ValidatorTypeCode.MAXIMUM               -> "cannot be greater than ${msg.arguments[0]}"

    ValidatorTypeCode.EXCLUSIVE_MINIMUM     -> "must be greater than ${msg.arguments[0]}"
    ValidatorTypeCode.EXCLUSIVE_MAXIMUM     -> "must be less than ${msg.arguments[0]}"
    // ValidatorTypeCode.MULTIPLE_OF


    //
    // Arrays
    //
    ValidatorTypeCode.UNIQUE_ITEMS          -> "items must be unique"
    ValidatorTypeCode.MIN_ITEMS             -> "must contain at least ${msg.arguments[0]} items"
    ValidatorTypeCode.MAX_ITEMS             -> "cannot contain more than ${msg.arguments[0]} items"
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
    ValidatorTypeCode.UNEVALUATED_PROPERTIES-> "unrecognized additional property"

    ValidatorTypeCode.REQUIRED              -> messageIndex.nullErrorMessage
    // ValidatorTypeCode.MIN_PROPERTIES
    // ValidatorTypeCode.MAX_PROPERTIES
    // ValidatorTypeCode.PATTERN_PROPERTIES
    // ValidatorTypeCode.PROPERTYNAMES
    // ValidatorTypeCode.PROPERTIES

    //
    // Misc
    //
    ValidatorTypeCode.TYPE                  -> when(msg.arguments[0]) {
      "null" -> messageIndex.nullErrorMessage
      else   -> if (msg.arguments.size == 2) {
        "expected type ${msg.arguments[1]}"
      } else {
        msg.arguments.asSequence().drop(1).joinToString(", ", "expected one of the types (", ")")
      }
    }

    ValidatorTypeCode.ENUM                  -> JSON.readValue<ArrayNode>(msg.arguments[0] as String)
      .joinToString(", ", "must be one of (", ")")

    ValidatorTypeCode.CONST                 -> "must be " + (msg.arguments[0] as String)

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
