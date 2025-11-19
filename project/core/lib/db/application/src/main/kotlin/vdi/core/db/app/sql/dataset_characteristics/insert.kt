package vdi.core.db.app.sql.dataset_characteristics

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.set
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetCharacteristics

// language=postgresql
private fun SQL(schema: String) =
"""
INSERT INTO
  ${schema}.${Table.Characteristics} (
    dataset_id
  , study_design
  , study_type
  , participant_ages
  , sample_year_start
  , sample_year_end
  )
VALUES
  (
    ?
  , ?
  , ?
  , ?
  , ?
  , ?
  ) 
"""

internal fun Connection.insertDatasetCharacteristics(schema: String, datasetID: DatasetID, characteristics: DatasetCharacteristics) =
  withPreparedUpdate(SQL(schema)) {
    set(1, datasetID)
    set(2, characteristics.studyDesign)
    set(3, characteristics.studyType)
    set(4, characteristics.participantAges)
    set(5, characteristics.years?.start)
    set(6, characteristics.years?.end)
  }