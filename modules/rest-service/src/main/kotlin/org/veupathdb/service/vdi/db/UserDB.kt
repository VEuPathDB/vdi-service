package org.veupathdb.service.vdi.db

import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.utils.db.DbManager
import org.veupathdb.service.vdi.db.userdb.selectUserIsGuest
import org.veupathdb.vdi.lib.common.field.UserID

object UserDB {
  private val log = LoggerFactory.getLogger(javaClass)

  private inline val userDB
    get() = DbManager.userDatabase().dataSource

  fun userIsGuest(userID: UserID): Boolean? {
    log.debug("testing whether user {} is a guest", userID)
    return userDB.connection.use { con -> con.selectUserIsGuest(userID) }
  }
}