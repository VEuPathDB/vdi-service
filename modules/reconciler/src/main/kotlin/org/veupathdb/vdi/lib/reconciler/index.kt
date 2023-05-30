package org.veupathdb.vdi.lib.reconciler

import org.veupathdb.vdi.lib.reconciler.config.ReconcilerConfig
import org.veupathdb.vdi.lib.reconciler.config.loadConfigFromEnvironment

fun Reconciler(config: ReconcilerConfig = loadConfigFromEnvironment()): Reconciler = ReconcilerImpl(config)