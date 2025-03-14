package org.veupathdb.service.vdi.util

import org.slf4j.LoggerFactory

internal val <T : Any> T.logger get() = LoggerFactory.getLogger(this.javaClass)
