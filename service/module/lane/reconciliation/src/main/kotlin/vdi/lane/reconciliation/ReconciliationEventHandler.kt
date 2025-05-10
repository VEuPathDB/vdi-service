package vdi.lane.reconciliation

import vdi.lib.modules.VDIModule

/**
 * Reconciliation Event Handler
 *
 * This module consumes the Kafka topic for reconciliation events and executes
 * a dataset reconciliation for the dataset specified in each incoming event.
 *
 * @since 1.0.0
 *
 * @author Elizabeth Paige Harper - https://github.com/Foxcapades
 */
sealed interface ReconciliationEventHandler : VDIModule
