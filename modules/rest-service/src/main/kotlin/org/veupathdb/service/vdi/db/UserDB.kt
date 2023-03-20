package org.veupathdb.service.vdi.db

import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.utils.db.DbManager
import org.veupathdb.service.vdi.db.userdb.selectUserDetails
import org.veupathdb.service.vdi.model.UserDetails
import vdi.components.common.fields.UserID


object UserDB {
  private val log = LoggerFactory.getLogger(javaClass)

  private inline val userDB
    get() = DbManager.userDatabase().dataSource

  fun lookupUserDetails(userIDs: Collection<UserID>): Map<UserID, UserDetails> {
    log.debug("looking user details for ${userIDs.size} users")
    return userDB.connection.use { con -> con.selectUserDetails(userIDs) }
  }
}
