package vdi.lane.meta

import vdi.lib.modules.VDIModule

/**
 * Update Meta Trigger Handling Module
 *
 * This module consumes the Kafka topic for meta update trigger events and calls
 * out to the appropriate plugin handler instance to process the dataset meta
 * update.
 *
 * @since 1.0.0
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
sealed interface UpdateMetaLane: VDIModule
