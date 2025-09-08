package vdi.core.db.app.sql.dataset_organism

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.usingPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.model.OrganismType
import vdi.core.db.jdbc.set
import vdi.model.data.DatasetID
import vdi.model.data.DatasetOrganism


private fun sql(schema: String) =
// language=postgresql
  """
INSERT INTO
  ${schema}.dataset_organism (
    dataset_id
  , organism_type
  , species
  , strain
  )
VALUES
  (?, ?, ?, ?)
"""

internal fun Connection.insertHostOrganism(
  schema: String,
  datasetID: DatasetID,
  organism: DatasetOrganism,
) =
  usingPreparedUpdate(sql(schema)) { insert ->
    insert[1] = datasetID
    insert[2] = OrganismType.Host
    insert[3] = organism.species
    insert[4] = organism.strain
  }

internal fun Connection.insertExperimentalOrganism(
  schema: String,
  datasetID: DatasetID,
  organism: DatasetOrganism,
) =
  usingPreparedUpdate(sql(schema)) { insert ->
    insert[1] = datasetID
    insert[2] = OrganismType.Experimental
    insert[3] = organism.species
    insert[4] = organism.strain
  }
