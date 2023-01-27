package vdi.conf

import vdi.util.SecretString

data class DatabaseConfiguration(
  val name: String,
  val ldap: SecretString,
  val user: SecretString,
  val pass: SecretString,
)