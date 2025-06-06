@file:JvmName("DatasetHyperlinkValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import java.net.URI
import java.net.URISyntaxException
import vdi.model.data.DatasetHyperlink
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.generated.model.DatasetHyperlink as APIHyperlink

private const val UrlMinLength = 7
private const val UrlMaxLength = 200
private const val TextMinLength = 3
private const val TextMaxLength = 300
private const val DescriptionMaxLength = 4000

internal fun APIHyperlink.cleanup() {
  url = url.cleanupString()
  text = text.cleanupString()
  description = description.cleanupString()
  isPublication = isPublication ?: false
}

internal fun Iterable<APIHyperlink?>.validate(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, h -> h.require(jPath, i, errors) { validate(jPath, i, errors) } }

private fun APIHyperlink.validate(jPath: String, index: Int, errors: ValidationErrors) {
  url.reqCheckLength(jPath..JsonField.URL, index, UrlMinLength, UrlMaxLength, errors)
  try {
    URI(url)
  } catch (e: URISyntaxException) {
    errors.add((jPath..JsonField.URL)..index, e.reason)
  }

  text.reqCheckLength(jPath..JsonField.TEXT, index, TextMinLength, TextMaxLength, errors)

  description.optCheckMaxLength(jPath..JsonField.DESCRIPTION, index, DescriptionMaxLength, errors)
}

internal fun APIHyperlink.toInternal() =
  DatasetHyperlink(
    url           = URI.create(url),
    text          = text,
    description   = description,
    isPublication = isPublication
  )
