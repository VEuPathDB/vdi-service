package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetHyperlink
import org.veupathdb.service.vdi.generated.model.DatasetHyperlinkImpl
import org.veupathdb.service.vdi.util.ValidationErrors
import org.veupathdb.vdi.lib.common.model.VDIDatasetHyperlink
import vdi.component.db.app.DatasetHyperlinkDescriptionMaxLength
import vdi.component.db.app.DatasetHyperlinkTextMaxLength
import vdi.component.db.app.DatasetHyperlinkUrlMaxLength

internal fun DatasetHyperlink.cleanup() {
  url = url?.takeIf { it.isNotBlank() }
    ?.trim()
  text = text?.takeIf { it.isNotBlank() }
    ?.trim()
  description = description?.takeIf { it.isNotBlank() }
    ?.trim()
  isPublication = isPublication ?: false
}

internal fun DatasetHyperlink?.validate(prefix: String, index: Int, validationErrors: ValidationErrors) {
  if (this == null) {
    validationErrors.add("${prefix}hyperlinks[$index]", "entries must not be null")
    return
  }

  if (url == null)
    validationErrors.add("${prefix}hyperlinks[$index].url", "field is required")
  else
    url.checkLength({ "${prefix}hyperlinks[$index].url" }, 3, DatasetHyperlinkUrlMaxLength, validationErrors)

  if (text == null)
    validationErrors.add("${prefix}hyperlinks[$index].text", "field is required")
  else
    url.checkLength({ "${prefix}hyperlinks[$index].text" }, 3, DatasetHyperlinkTextMaxLength, validationErrors)

  description?.checkLength({ "${prefix}hyperlinks[$index].description" }, DatasetHyperlinkDescriptionMaxLength, validationErrors)
}

internal fun DatasetHyperlink.toInternal() =
  VDIDatasetHyperlink(url, text, description, isPublication)

internal fun VDIDatasetHyperlink.toExternal(): DatasetHyperlink =
  DatasetHyperlinkImpl().also {
    it.url = url
    it.text = text
    it.description = description
    it.isPublication = isPublication
  }
