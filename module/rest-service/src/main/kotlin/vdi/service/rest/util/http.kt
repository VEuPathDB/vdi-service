package vdi.service.rest.util

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URI
import java.net.URL

internal data class URLFetchResult(val status: Int, val fileName: String?, val body: InputStream?) : AutoCloseable {
  inline val isSuccess get() = status in 200 .. 299

  inline val hasBody get() = body != null

  override fun close() {
    body?.close()
  }
}

internal inline val URL.shortForm get() = "$protocol://$host"

/**
 * Attempts to fetch the contents of a remote address.
 *
 * This method follows redirects even when switching between http/https, unlike
 * the built in [HttpURLConnection.followRedirects] option which does not allow
 * protocol changes.
 */
internal fun URL.fetchContent(): URLFetchResult {
  var currentURL = this

  while (true) {
    with (currentURL.protocol) {
      if (this != "http" && this != "https")
        throw URLFetchException("unsupported protocol, only http and https are supported")
    }

    val con = currentURL.openConnection() as HttpURLConnection

    try {
      val code = con.responseCode
      val name = con.headerFields["Content-Disposition"]
        ?.firstOrNull { it.startsWith("filename=") }
        ?.substringAfter('=')

      if (code in 200 .. 299)
        return URLFetchResult(code, name, con.inputStream)

      if (code !in 300 .. 399)
        return URLFetchResult(code, null, con.errorStream)

      val location = con.getHeaderField("Location")
        ?: throw URLFetchException("remote server responded with a 3xx status code but no redirect location")

      if (location.isBlank())
        throw URLFetchException("remote server responded with a 3xx status code and a blank redirect location")

      currentURL = try { location.toJavaURL() }
      catch (e: MalformedURLException) {
        throw URLFetchException("remove server responded with a 3xx status code and an invalid or unsupported url: "
          + location)
      }
    } catch (e: IOException) {
      throw URLFetchException("encountered unexpected exception while attempting to communicate with remote server", e)
    }
  }
}

internal class URLFetchException : Exception {
  constructor(message: String) : super(message)
  constructor(message: String, cause: Throwable) : super(message, cause)
}

internal fun String.toJavaURL(): URL {
  val dividerIndex = indexOf("://")

  if (dividerIndex < 1)
    throw MalformedURLException("url does not specify a protocol, must be http or https")

  with(substring(0, dividerIndex)) {
    if (this != "http" && this != "https")
      throw MalformedURLException("url has an unsupported protocol, must be http or https")
  }

  return URI.create(this).toURL()
}
