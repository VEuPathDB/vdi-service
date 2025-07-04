package vdi.lane.soft_delete

import vdi.core.modules.VDIModule

/**
 * Soft Delete Trigger Handling Module
 *
 * This module consumes the Kafka topic for soft delete trigger events and
 * sets the soft-delete flag on the cache DB record and relevant application DB
 * records.
 *
 * @since 1.0.0
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
sealed interface SoftDeleteLane: VDIModule
