package vdi.lane.reconciliation.util

import vdi.lane.reconciliation.CriticalReconciliationError
import vdi.lane.reconciliation.ReconcilerTarget


internal fun <T> T?.require(state: ReconcilerTarget, msg: String): T =
  if (this == null) {
    state.logger.error(msg)
    throw CriticalReconciliationError()
  } else {
    this
  }

