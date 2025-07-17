package vdi.service.rest.server.transforms

import java.util.HashSet
import vdi.model.data.DatasetMetadata
import vdi.service.rest.gen.model.DatasetPostMeta

fun DatasetPostMeta.toDatasetMeta(): DatasetMetadata {
  return DatasetMetadata(
    type = datasetType.toDatasetType(),
    installTargets = installTargets.mapTo(HashSet(installTargets.size)) { it.value },
    visibility = visibility,
    owner = TODO(),
    name = TODO(),
    summary = TODO(),
    origin = TODO(),
    created = TODO(),
    shortName = TODO(),
    description = TODO(),
    shortAttribution = TODO(),
    sourceURL = TODO(),
    dependencies = TODO(),
    publications = TODO(),
    hyperlinks = TODO(),
    organisms = TODO(),
    contacts = TODO(),
    originalID = TODO(),
    revisionHistory = TODO(),
    properties = TODO(),
    project = TODO(),
    program = TODO()
  )
}