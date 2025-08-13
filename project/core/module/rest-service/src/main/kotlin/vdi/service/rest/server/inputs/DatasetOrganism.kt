@file:JvmName("DatasetOrganismValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.reqCheckLength
import vdi.model.data.DatasetOrganism
import vdi.service.rest.generated.model.JsonField
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

fun APIOrganism.toInternal() =
  DatasetOrganism(species, strain)

