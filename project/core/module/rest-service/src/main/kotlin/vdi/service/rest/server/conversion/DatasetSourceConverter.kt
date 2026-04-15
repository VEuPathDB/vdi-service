package vdi.service.rest.server.conversion

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.reqCheckLength
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.require
import vdi.service.rest.generated.model.DatasetSourceImpl
import vdi.model.meta.DatasetSource as VdiDatasetSource
import vdi.service.rest.generated.model.DatasetSource as RamlDatasetSource

object DatasetSourceConverter: APIListTypeConverter<RamlDatasetSource?, VdiDatasetSource> {
  private inline val ValidUrlLengths
    get() = 10..8192

  private inline val ValidVersionLengths
    get() = 1..128

  // noop; nothing to clean in this type
  override fun cleanup(value: List<RamlDatasetSource?>): List<RamlDatasetSource?> = value
  override fun cleanupSingle(value: RamlDatasetSource?): RamlDatasetSource? = value

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
    value!!.url?.reqCheckLength(jsonPath, index, ValidUrlLengths, errors)
    value.version?.reqCheckLength(jsonPath, index, ValidVersionLengths, errors)
  }

  override fun toSingleInternal(value: RamlDatasetSource?): VdiDatasetSource =
    VdiDatasetSource(value!!.url, value.version)

  override fun toSingleExternal(value: VdiDatasetSource): RamlDatasetSource =
    DatasetSourceImpl().apply {
      url = value.url
      version = value.version
    }
}