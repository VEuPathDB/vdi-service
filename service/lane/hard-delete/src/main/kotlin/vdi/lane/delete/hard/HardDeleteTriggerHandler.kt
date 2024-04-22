package vdi.lane.delete.hard

import vdi.component.modules.VDIModule

/**
 * Hard Delete Trigger Handler
 *
 * This module consumes the Kafka topic for hard deletion trigger events and
 * logs them.
 *
 * This module is a leftover from a previous design iteration and is being kept
 * for the possible future where it is needed again.
 *
 * @since 1.0.0
 *
 * @author Elizabeth Paige Harper - https://github.com/Foxcapades
 */
sealed interface HardDeleteTriggerHandler : VDIModule