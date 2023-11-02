package vdi.component.vault

import io.github.jopenlibs.vault.Vault
import io.github.jopenlibs.vault.VaultConfig

object Vault {
  private val vault = Vault.create(VaultConfig().address("http://external-vault:8200").build())

  @JvmStatic
  fun getUsernamePassword(path: String): Pair<String, String> {
    val res = vault.logical().read(path)
    return res.dataObject.getString("username") to res.dataObject.getString("password")
  }

  @JvmStatic
  fun getAccessTokenSecretKey(path: String): Pair<String, String> {
    val res = vault.logical().read(path)
    return res.dataObject.getString("accessToken") to res.dataObject.getString("secretKey")
  }

  @JvmStatic
  fun getValue(path: String): String {
    val res = vault.logical().read(path)
    return res.dataObject.getString("value")
  }
}