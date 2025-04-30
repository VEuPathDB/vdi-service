package vdi.lane.prune.revision

import vdi.lib.modules.VDIModule

/**
 * Revision Pruning Trigger Handler
 *
 * This module consumes the Kafka topic for dataset revision pruning events and
 * uninstalls outdated dataset revisions.
 *
 * Not all events handled by the revision pruner will result in a revision's
 * removal.  It is the job of the module implementation to verify that it is
 * safe to perform the removal before attempting it.
 *
 * @since 1.7.0
 *
 * @author Elizabeth Paige Harper - https://github.com/Foxcapades
 */
sealed interface RevisionPruningTriggerHandler : VDIModule
