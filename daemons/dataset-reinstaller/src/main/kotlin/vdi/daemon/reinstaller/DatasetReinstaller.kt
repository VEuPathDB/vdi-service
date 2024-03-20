package vdi.daemon.reinstaller

import vdi.component.modules.VDIModule

/**
 * Dataset Reinstaller
 *
 * VDI module that scans the connected application databases for datasets in the
 * [InstallStatus.ReadyForReinstall] state, uninstalls them to remove any broken
 * state, then reinstalls them.
 *
 * @since 1.0.0
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
sealed interface DatasetReinstaller : VDIModule