package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.service.rest.generated.model.DatasetHyperlink
import vdi.service.rest.generated.model.JsonField
import org.veupathdb.vdi.lib.common.model.VDIDatasetHyperlink

private const val UrlMinLength = 7
private const val UrlMaxLength = 200
private const val TextMinLength = 3
private const val TextMaxLength = 300
private const val DescriptionMaxLength = 4000

internal fun vdi.service.rest.generated.model.DatasetHyperlink.cleanup() {
  url = url.cleanupString()
  text = text.cleanupString()
  description = description.cleanupString()
  isPublication = isPublication ?: false
}

internal fun Iterable<vdi.service.rest.generated.model.DatasetHyperlink?>.validate(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, h -> h.require(jPath, i, errors) { validate(jPath, i, errors) } }

private fun vdi.service.rest.generated.model.DatasetHyperlink.validate(jPath: String, index: Int, errors: ValidationErrors) {
  url.reqCheckLength(jPath..vdi.service.rest.generated.model.JsonField.URL, index, UrlMinLength, UrlMaxLength, errors)
  text.reqCheckLength(jPath..vdi.service.rest.generated.model.JsonField.TEXT, index, TextMinLength, TextMaxLength, errors)

  description.optCheckMaxLength(jPath..vdi.service.rest.generated.model.JsonField.DESCRIPTION, index, DescriptionMaxLength, errors)
}

internal fun vdi.service.rest.generated.model.DatasetHyperlink.toInternal() =
  VDIDatasetHyperlink(url, text, description, isPublication)
