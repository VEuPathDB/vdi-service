package vdi.service.rest.server.outputs

import vdi.model.data.LinkedDataset
import vdi.service.rest.generated.model.RelatedStudyImpl
import vdi.service.rest.generated.model.RelatedStudy as APIStudy

fun RelatedStudy(study: LinkedDataset): APIStudy =
  RelatedStudyImpl().also {
    it.studyUri      = study.studyURI.toString()
    it.sharesRecords = study.sharesRecords
  }