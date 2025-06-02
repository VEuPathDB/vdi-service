package vdi.model

import java.time.*

/**
 * Project Origin Timestamp
 */
inline val OriginTimestamp: OffsetDateTime
  get() = ZonedDateTime.of(LocalDate.of(2022, Month.JULY, 1), LocalTime.MIN, ZoneId.of("UTC")).toOffsetDateTime()

const val DatasetMetaFilename = "vdi-meta.json"

const val DatasetManifestFilename = "vdi-manifest.json"
