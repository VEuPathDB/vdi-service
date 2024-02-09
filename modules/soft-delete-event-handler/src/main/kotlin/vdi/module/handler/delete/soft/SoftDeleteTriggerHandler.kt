package vdi.module.handler.delete.soft

import vdi.component.modules.VDIServiceModule

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
sealed interface SoftDeleteTriggerHandler : VDIServiceModule