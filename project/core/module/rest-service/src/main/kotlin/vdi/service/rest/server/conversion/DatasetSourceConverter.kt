package vdi.service.rest.server.conversion

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.reqCheckLength
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.require
import java.net.URI
import java.net.URISyntaxException
import vdi.service.rest.generated.model.DatasetSourceImpl
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.server.inputs.cleanupString
import vdi.model.meta.DatasetSource as VdiDatasetSource
import vdi.service.rest.generated.model.DatasetSource as RamlDatasetSource

object DatasetSourceConverter: APIListTypeConverter<RamlDatasetSource?, VdiDatasetSource> {
  private inline val ValidVersionLengths
    get() = 1..128

  override fun cleanupSingle(value: RamlDatasetSource?): RamlDatasetSource? =
    value?.apply {
      cleanupString(::getUrl)
      cleanupString(::getVersion)
    }

  override fun validate(
    value: List<RamlDatasetSource?>,
    jsonPath: String,
    errors: ValidationErrors,
  ) {
    val hits = HashMap<Pair<String?, String?>, Int>(value.size)

    value.asSequence()
      .onEachIndexed { i, it -> it.require(jsonPath, i, errors) {} }
      .filterNotNull()
      .onEachIndexed { i, it -> validateSingle(it, jsonPath, i, errors) }
      .map { it.url to it.version }
      .forEachIndexed { i, it ->
        if (it in hits)
          errors.add(jsonPath..i, "duplicate of value at index ${hits[it]}")
        else
          hits[it] = i
      }
  }

  override fun validateSingle(
    value: RamlDatasetSource?,
    jsonPath: String,
    index: Int,
    errors: ValidationErrors,
  ) {
    val jsonPath = jsonPath..index

    value!!.url.require(jsonPath..JsonField.URL, errors) {
      try {
        URI(this)
      } catch (_: URISyntaxException) {
        errors.add(jsonPath..JsonField.URL, "malformed url")
      }
    }
    value.version.reqCheckLength(jsonPath..JsonField.VERSION, ValidVersionLengths, errors)
  }

  override fun toSingleInternal(value: RamlDatasetSource?): VdiDatasetSource =
    VdiDatasetSource(value!!.url, value.version)

  override fun toSingleExternal(value: VdiDatasetSource): RamlDatasetSource =
    DatasetSourceImpl().apply {
      url = value.url
      version = value.version
    }
}