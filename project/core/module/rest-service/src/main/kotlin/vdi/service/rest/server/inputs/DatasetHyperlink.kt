@file:JvmName("DatasetHyperlinkInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import java.net.URI
import vdi.model.data.DatasetHyperlink
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.generated.model.DatasetHyperlink as APIHyperlink

private val UrlLengthRange = 7..2000

internal fun APIHyperlink?.cleanup() = this?.apply {
  cleanupString(::getUrl)
  cleanupString(::getDescription)
}

internal fun Iterable<APIHyperlink?>.validate(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, h -> h.require(jPath, i, errors) { validate(jPath, i, errors) } }

private fun APIHyperlink.validate(jPath: String, index: Int, errors: ValidationErrors) {
  url.reqCheckLength(jPath..JsonField.URL, index, UrlLengthRange, errors)

  try {
    URI(url).toURL()
  } catch (e: IllegalArgumentException) {
    errors.add((jPath..JsonField.URL)..index, "invalid URL value")
  }
}

internal fun APIHyperlink.toInternal() =
  DatasetHyperlink(URI.create(url), description)

internal fun Iterable<APIHyperlink>.toInternal() =
  map { DatasetHyperlink(URI.create(it.url), it.description) }
