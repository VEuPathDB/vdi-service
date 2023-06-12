package org.veupathdb.service.vdi.db.userdb

import org.veupathdb.vdi.lib.common.field.UserID
import java.sql.Connection

// language=oracle
private const val SQL = """
SELECT
  is_guest
FROM
  userlogins5.users
WHERE
  user_id = ?
"""

internal fun Connection.selectUserIsGuest(userID: UserID) =
  prepareStatement(SQL).use { ps ->
    ps.setLong(1, userID.toLong())
    ps.executeQuery().use { rs ->
      if (rs.next())
        rs.getBoolean(1)
      else
        null
    }
  }