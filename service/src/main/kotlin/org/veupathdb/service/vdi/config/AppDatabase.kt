package org.veupathdb.service.vdi.config

class AppDatabase(
  val name: String,
  val ldap: String,
  val username: String,
  val password: String,
  val poolSize: Int
)