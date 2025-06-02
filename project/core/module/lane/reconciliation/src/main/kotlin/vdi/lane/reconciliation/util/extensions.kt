package vdi.lane.reconciliation.util

import vdi.lane.reconciliation.CriticalReconciliationError
import vdi.lane.reconciliation.ReconciliationContext


internal fun <T> T?.require(state: ReconciliationContext, msg: String): T =
  if (this == null) {
    state.logger.error(msg)
    throw CriticalReconciliationError()
  } else {
    this
  }

internal inline fun ReconciliationContext.safeTest(file: String, fn: () -> Boolean): Boolean =
  safeExec({ "failed to test for file \"$file\"" }, fn)

internal inline fun <T> ReconciliationContext.safeExec(msg: () -> String, fn: () -> T) =
  try {
    fn()
  } catch (e: CriticalReconciliationError) {
    throw e
  } catch (e: Throwable) {
    logger.error(msg(), e)
    throw CriticalReconciliationError()
  }

internal inline fun <T> ReconciliationContext.safeExec(msg: String, fn: () -> T) =
  try {
    fn()
  } catch (e: CriticalReconciliationError) {
    throw e
  } catch (e: Throwable) {
    logger.error(msg, e)
    throw CriticalReconciliationError()
  }
