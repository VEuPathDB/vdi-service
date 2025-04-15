package vdi.component.db.app

import java.sql.SQLException

fun AppDBTransaction.isUniqueConstraintViolation(e: Throwable) =
  e is SQLException && (
    (platform == AppDBPlatform.Oracle && e.errorCode == 1)
    || (platform == AppDBPlatform.Postgres && e.sqlState == "23505")
  )
