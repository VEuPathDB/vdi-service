@file:Suppress("NOTHING_TO_INLINE")
package org.veupathdb.service.vdi.util

import org.slf4j.LoggerFactory

internal inline val <T : Any> T.logger get() = LoggerFactory.getLogger(this.javaClass)

internal inline fun logger(name: String) = LoggerFactory.getLogger(name)
