package vdi.service.rest.server.conversion

import vdi.service.rest.generated.model.DatasetCharacteristicsImpl
import vdi.model.meta.DatasetCharacteristics as InternalType
import vdi.service.rest.generated.model.DatasetCharacteristics as RamlType

internal fun DatasetCharacteristics(props: InternalType): RamlType =
  DatasetCharacteristicsImpl().apply {
    studyDesign = props.studyDesign
    studyType = props.studyType

    props.countries
      .takeIf(List<*>::isNotEmpty)
      ?.also(::setCountries)

    props.years
      ?.let(::SampleYearRange)
      ?.also(::setYears)

    props.studySpecies
      .takeIf(List<*>::isNotEmpty)
      ?.also(::setStudySpecies)

    props.outcomes
      .takeIf(List<*>::isNotEmpty)
      ?.also(::setOutcomes)

    props.associatedFactors
      .takeIf(List<*>::isNotEmpty)
      ?.also(::setAssociatedFactors)

    props.participantAges
      ?.also(::setParticipantAges)

    props.sampleTypes
      .takeIf(List<*>::isNotEmpty)
      ?.also(::setSampleTypes)
  }