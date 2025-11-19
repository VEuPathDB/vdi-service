package vdi.service.rest.server.outputs

import vdi.model.meta.DatasetCharacteristics
import vdi.model.meta.SampleYearRange
import vdi.service.rest.generated.model.DatasetCharacteristicsImpl
import vdi.service.rest.generated.model.SampleYearRangeImpl
import vdi.service.rest.generated.model.DatasetCharacteristics as APICharacteristics
import vdi.service.rest.generated.model.SampleYearRange as APIYears

internal fun DatasetCharacteristics(characteristics: DatasetCharacteristics): APICharacteristics =
  DatasetCharacteristicsImpl().also {
    it.studyDesign       = characteristics.studyDesign
    it.studyType         = characteristics.studyType
    it.countries         = characteristics.countries
    it.years             = characteristics.years?.let(::SampleYearRange)
    it.studySpecies      = characteristics.studySpecies
    it.diseases          = characteristics.diseases
    it.associatedFactors = characteristics.associatedFactors
    it.participantAges   = characteristics.participantAges
    it.sampleTypes       = characteristics.sampleTypes
  }

internal fun SampleYearRange(years: SampleYearRange): APIYears =
  SampleYearRangeImpl().also {
    it.start = years.start
    it.end   = years.end
  }