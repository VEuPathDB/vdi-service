package vdi.daemon.reconciler

import vdi.core.modules.VDIModule

/**
 * Reconciler module
 *
 * This module does a complete diff of the dataset storage and the RDBMS database replication targets, sending messages
 * to reconcile any differing state.
 *
 * @since 1.0.0
 */
sealed interface Reconciler : VDIModule
