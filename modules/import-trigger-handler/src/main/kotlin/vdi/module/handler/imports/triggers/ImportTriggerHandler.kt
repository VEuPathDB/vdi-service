package vdi.module.handler.imports.triggers

import org.veupathdb.vdi.lib.common.VDIServiceModule

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
interface ImportTriggerHandler : VDIServiceModule

