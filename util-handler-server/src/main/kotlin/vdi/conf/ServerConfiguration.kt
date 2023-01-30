package vdi.conf

private const val DEFAULT_SERVER_PORT: UShort = 80u
private const val DEFAULT_SERVER_HOST: String = "0.0.0.0"

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
  constructor(environment: Map<String, String> = System.getenv()) : this(
    port = environment["SERVER_PORT"]?.toUShort() ?: DEFAULT_SERVER_PORT,
    host = environment["SERVER_HOST"]             ?: DEFAULT_SERVER_HOST,
  )
}
