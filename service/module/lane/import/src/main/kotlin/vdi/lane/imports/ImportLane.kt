package vdi.lane.imports

import vdi.lib.modules.VDIModule

/**
 * Import Trigger Handling Module
 *
 * This module consumes the Kafka topic for import trigger events and calls out
 * to the appropriate plugin handler instance to process the dataset import.
 *
 * @since 1.0.0
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
interface ImportLane : VDIModule

