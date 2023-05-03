package vdi.module.handler.share.trigger

import org.veupathdb.vdi.lib.common.VDIServiceModule

/**
 * Share Trigger Handling Module
 *
 * This module consumes the Kafka topic for share trigger events and writes the
 * new share data to the relevant application databases.
 *
 * @since 1.0.0
 *
 * @author Elizabeth Paige Harper - https://github.com/Foxcapades
 */
sealed interface ShareTriggerHandler : VDIServiceModule