package vdi.conf

import vdi.Const

/**
 * HTTP Server Specific Configuration Options
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since 1.0.0
 */
data class ServerConfiguration(
  val port: UShort,
  val host: String
) {
  constructor(env: Map<String, String> = System.getenv()) : this(
    port = (env[Const.EnvKey.SERVER_PORT] ?: Const.ConfigDefault.SERVER_PORT).toUShort(),
    host = env[Const.EnvKey.SERVER_HOST] ?: Const.ConfigDefault.SERVER_HOST,
  )
}
