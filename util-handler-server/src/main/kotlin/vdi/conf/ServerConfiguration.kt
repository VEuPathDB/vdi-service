package vdi.conf

import vdi.Consts

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
    port = (env[Consts.ENV_SERVER_PORT] ?: Consts.DEFAULT_SERVER_PORT).toUShort(),
    host = env[Consts.ENV_SERVER_HOST] ?: Consts.DEFAULT_SERVER_HOST,
  )
}
