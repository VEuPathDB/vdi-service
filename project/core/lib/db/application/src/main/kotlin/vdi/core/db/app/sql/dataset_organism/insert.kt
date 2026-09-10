package vdi.core.db.app.sql.dataset_organism

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.usingPreparedBatchUpdate
import io.foxcapades.kdbc.usingPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.model.OrganismType
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.set
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetOrganism


private fun sql(schema: String) =
// language=sql
  """
INSERT INTO
  ${schema}.${Table.Organisms} (
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

internal fun Connection.insertExperimentalOrganisms(
  schema: String,
  datasetID: DatasetID,
  organisms: Iterable<DatasetOrganism>,
) =
  usingPreparedBatchUpdate(sql(schema), organisms) { insert, org ->
    insert[1] = datasetID
    insert[2] = OrganismType.Experimental
    insert[3] = org.species
    insert[4] = org.strain
  }.reduce(Int::plus)
