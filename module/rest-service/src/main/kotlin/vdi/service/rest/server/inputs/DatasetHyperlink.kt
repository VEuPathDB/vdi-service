@file:JvmName("DatasetHyperlinkValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import org.veupathdb.vdi.lib.common.model.VDIDatasetHyperlink
import vdi.service.rest.generated.model.DatasetHyperlink
import vdi.service.rest.generated.model.JsonField

private const val UrlMinLength = 7
private const val UrlMaxLength = 200
private const val TextMinLength = 3
private const val TextMaxLength = 300
private const val DescriptionMaxLength = 4000

internal fun DatasetHyperlink.cleanup() {
  url = url.cleanupString()
  text = text.cleanupString()
  description = description.cleanupString()
  isPublication = isPublication ?: false
}

internal fun Iterable<DatasetHyperlink?>.validate(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, h -> h.require(jPath, i, errors) { validate(jPath, i, errors) } }

private fun DatasetHyperlink.validate(jPath: String, index: Int, errors: ValidationErrors) {
  url.reqCheckLength(jPath..JsonField.URL, index, UrlMinLength, UrlMaxLength, errors)
  text.reqCheckLength(jPath..JsonField.TEXT, index, TextMinLength, TextMaxLength, errors)

  description.optCheckMaxLength(jPath..JsonField.DESCRIPTION, index, DescriptionMaxLength, errors)
}

internal fun DatasetHyperlink.toInternal() =
  VDIDatasetHyperlink(
    url           = url,
    text          = text,
    description   = description,
    isPublication = isPublication
  )
