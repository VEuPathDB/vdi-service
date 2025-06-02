package vdi.model.api.internal

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Defines a common response body consisting of a JSON object containing a
 * single key "message" mapped to a string value containing an error message.
 */
data class SimpleErrorResponse(@JsonProperty(JSONKeys.Message) val message: String)