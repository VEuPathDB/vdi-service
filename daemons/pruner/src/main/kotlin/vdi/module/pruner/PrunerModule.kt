package vdi.module.pruner

import vdi.component.modules.VDIServiceModule

/**
 * Pruning Module
 *
 * This module is responsible for automatically pruning jobs that have been
 * soft deleted for more than a configured amount of time.
 *
 * @since 1.0.0
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
sealed interface PrunerModule : VDIServiceModule

