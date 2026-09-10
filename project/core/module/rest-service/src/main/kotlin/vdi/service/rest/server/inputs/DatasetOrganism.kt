@file:JvmName("DatasetOrganismInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.reqCheckLength
import org.veupathdb.lib.request.validation.require
import vdi.model.meta.DatasetOrganism
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.generated.model.OrganismPatch
import vdi.service.rest.generated.model.DatasetOrganism as APIOrganism

private val LengthRange = 3..128

fun APIOrganism?.cleanup() = this?.apply {
  cleanupString(::getSpecies)
  cleanupString(::getStrain)
}

fun APIOrganism?.validate(jPath: String, errors: ValidationErrors) {
  if (this != null) {
    species.reqCheckLength(jPath..JsonField.SPECIES, LengthRange, errors)
    strain.reqCheckLength(jPath..JsonField.STRAIN, LengthRange, errors)
  }
}

fun APIOrganism.validate(jPath: String, index: Int, errors: ValidationErrors) {
  species.reqCheckLength(jPath..JsonField.SPECIES, index, LengthRange, errors)
  strain.reqCheckLength(jPath..JsonField.STRAIN, index, LengthRange, errors)
}

fun Iterable<APIOrganism?>.validate(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, row -> row.require(jPath, i, errors) { validate(jPath, i, errors) } }

fun OrganismPatch?.applyPatch(original: DatasetOrganism?) =
  when {
    this == null -> original

    this.value == null -> null

    else -> DatasetOrganism(
      species = value.species,
      strain  = value.strain,
    )
  }

fun APIOrganism.toInternal() =
  DatasetOrganism(species, strain)


fun Iterable<APIOrganism>.toInternal(): List<DatasetOrganism> =
  map(APIOrganism::toInternal)

