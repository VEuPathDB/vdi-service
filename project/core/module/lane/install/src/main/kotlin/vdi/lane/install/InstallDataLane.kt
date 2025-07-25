package vdi.lane.install

import vdi.core.modules.VDIModule

/**
 * Install Data Trigger Handler
 *
 * This module consumes the Kafka topic for install data trigger events and
 * calls out to the plugin handler server for the target plugin and application
 * databases to install the target datasets.
 *
 * @since 1.0.0
 *
 * @author Elizabeth Paige Harper - https://github.com/Foxcapades
 */
sealed interface InstallDataLane: VDIModule
