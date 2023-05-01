package org.veupathdb.service.vdi.db

import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.utils.db.DbManager
import org.veupathdb.service.vdi.db.accountdb.selectUserDetails
import org.veupathdb.service.vdi.model.UserDetails
import org.veupathdb.vdi.lib.common.field.UserID


object AccountDB {
  private val log = LoggerFactory.getLogger(javaClass)

  private inline val userDB
    get() = DbManager.accountDatabase().dataSource

  fun lookupUserDetails(userIDs: Collection<UserID>): Map<UserID, UserDetails> {
    log.debug("looking user details for ${userIDs.size} users")
    return userDB.connection.use { con -> con.selectUserDetails(userIDs) }
  }
}
