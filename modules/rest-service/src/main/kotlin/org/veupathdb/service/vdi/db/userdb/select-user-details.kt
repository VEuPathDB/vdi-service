package org.veupathdb.service.vdi.db.userdb

import org.veupathdb.service.vdi.model.UserDetails
import java.sql.Connection
import kotlin.math.max
import kotlin.math.min
import vdi.components.common.fields.UserID
import vdi.components.common.fields.asWDKUserID

// language=oracle
private const val SQL_PREFIX_LOOKUP_USER_DETAILS = """
SELECT
  user_id
, (
    SELECT value
    FROM useraccounts.account_properties ap2
    WHERE ap1.user_id = ap2.user_id
      AND key = 'first_name'
  ) as first_name
, (
    SELECT value
    FROM useraccounts.account_properties ap2
    WHERE ap1.user_id = ap2.user_id
      AND key = 'last_name'
  )  as last_name
, (
    SELECT value
    FROM useraccounts.account_properties ap2
    WHERE ap1.user_id = ap2.user_id
      AND key = 'organization'
  ) as organization
FROM
  useraccounts.account_properties ap1
WHERE
  user_id IN (
"""

// language=oracle
private const val SQL_SUFFIX_LOOKUP_USER_DETAILS = """
  )
GROUP BY
  user_id
"""

internal fun Connection.selectUserDetails(userIDs: Collection<UserID>): Map<UserID, UserDetails> {
  if (userIDs.isEmpty())
    return emptyMap()

  val out = HashMap<UserID, UserDetails>(userIDs.size)
  val list = userIDs.toList()

  for (offset in userIDs.indices step 1000) {
    val limit = min(1000, max(userIDs.size - offset, 0))

    if (limit == 0)
      break

    val sb = StringBuilder(2048)
      .append(SQL_PREFIX_LOOKUP_USER_DETAILS)
      .append("    ?")

    for (i in 1 until limit) {
      sb.append(",\n    ?")
    }

    sb.append(SQL_SUFFIX_LOOKUP_USER_DETAILS)

    prepareStatement(sb.toString()).use { ps ->
      for (j in 0 until limit) {
        ps.setLong(j + 1, list[j + offset].toString().toLong())
      }

      ps.executeQuery().use { rs ->
        val userID = rs.getLong(1).asWDKUserID()

        out[userID] = UserDetails(
          userID,
          rs.getString("first_name"),
          rs.getString("last_name"),
          rs.getString("organization")
        )
      }
    }
  }

  return out
}

