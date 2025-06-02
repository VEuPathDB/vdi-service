package vdi.service.rest

import jakarta.ws.rs.core.*
import jakarta.ws.rs.ext.RuntimeDelegate
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.StringTokenizer
import java.util.TimeZone

internal class UnionResponse<T>(
  private val statusType: StatusType,
  private val entity: T = null,
  private val contentType: MediaType? = null,
): Response() {
  @JvmField
  protected val metadata = MultivaluedHashMap<String, Any>()

  @JvmField
  protected val headers = MultivaluedHashMap<String, String>()

  override fun bufferEntity() = false

  override fun close() {
    when (entity) { is AutoCloseable -> entity.close() }
  }

  override fun getAllowedMethods(): Set<String> =
    headers[HttpHeaders.ALLOW]?.toSet() ?: emptySet()

  override fun getCookies(): Map<String, NewCookie> =
    headers[HttpHeaders.SET_COOKIE]
      .takeUnless { it.isNullOrEmpty() }
      ?.map(::parseNewCookie)
      ?.let { cookies ->
        LinkedHashMap<String, NewCookie>(cookies.size).apply {
          cookies.forEach { merge(it.name, it, ::getPreferredCookie) }
        }
      }
      ?: emptyMap()

  override fun getEntity() = entity

  override fun getDate() =
    headers.getFirst(HttpHeaders.DATE)?.let(dateFormat::parse)

  override fun getEntityTag() =
    headers.getFirst(HttpHeaders.ETAG)?.let {
      RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag::class.java).fromString(it)
    }

  override fun getHeaderString(name: String): String? = headers.getFirst(name)

  override fun getLastModified() =
    headers.getFirst(HttpHeaders.LAST_MODIFIED)?.let(dateFormat::parse)

  override fun getMediaType() = contentType

  override fun getMetadata(): MultivaluedMap<String, Any> = metadata

  override fun getStatus() = statusType.statusCode

  override fun getStatusInfo() = statusType

  override fun getStringHeaders(): MultivaluedMap<String, String> = headers

  override fun getLanguage(): Locale? = null

  override fun getLength() =
    headers.getFirst(HttpHeaders.CONTENT_LENGTH)?.toInt() ?: -1

  override fun getLinks(): Set<Link>? =
    headers[HttpHeaders.LINK]
      ?.let { it.mapTo(HashSet<Link>(it.size), Link::valueOf) }

  override fun getLink(relation: String) =
    links?.firstOrNull { it.rels.contains(relation) }

  override fun getLinkBuilder(relation: String) =
    getLink(relation)?.let(Link::fromLink)

  override fun getLocation() =
    headers.getFirst(HttpHeaders.LOCATION)?.let(URI::create)

  override fun hasEntity() = entity != null

  override fun hasLink(relation: String) = getLink(relation) != null

  override fun <T: Any?> readEntity(entityType: Class<T>?): T =
    throw UnsupportedOperationException()

  override fun <T: Any?> readEntity(entityType: GenericType<T>?): T =
    throw UnsupportedOperationException()

  override fun <T: Any?> readEntity(entityType: Class<T>?, annotations: Array<out Annotation>?): T =
    throw UnsupportedOperationException()

  override fun <T: Any?> readEntity(entityType: GenericType<T>?, annotations: Array<out Annotation>?): T =
    throw UnsupportedOperationException()

  companion object {
    val dateFormat by lazy {
      SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).apply { timeZone = TimeZone.getTimeZone("GMT") }
    }

    fun getPreferredCookie(first: NewCookie, second: NewCookie): NewCookie =
      Comparator.nullsFirst(
        Comparator.comparingInt(ToIntFunction { obj: NewCookie -> obj.maxAge })
          .thenComparing(Function { obj: NewCookie -> obj.expiry }, Comparator.nullsLast(Comparator.naturalOrder()))
          .thenComparing(
            Function { obj: NewCookie -> obj.path },
            Comparator.nullsLast(Comparator.comparing(Function { obj: String -> obj.length }))
          )
      )
        .compare(first, second)
        .let { when {
          it > 0 -> first
          else   -> second
        } }

    fun parseNewCookie(header: String): NewCookie {
      val bites = StringTokenizer(header, ";,", false).iterator()

      var builder: NewCookie.Builder? = null

      while (bites.hasNext()) {
        val crumbs = (bites.next() as String).split('=', limit = 2)
        var tName  = if (crumbs.isNotEmpty()) crumbs[0].trim() else ""
        var tValue = if (crumbs.size > 1) crumbs[1].trim() else ""

        if (tValue.startsWith("\"") && tValue.endsWith("\"") && tValue.length > 1) {
          tValue = tValue.substring(1, tValue.length - 1)
        }

        if (builder == null) {
          builder = NewCookie.Builder(tName)
            .also { it.value(tValue) }
        } else {
          tName = tName.lowercase()
          when {
            tName.startsWith("comment") -> builder.comment(tValue)
            tName.startsWith("domain") -> builder.domain(tValue)
            tName.startsWith("max-age") -> builder.maxAge(tValue.toInt())
            tName.startsWith("path") -> builder.path(tValue)
            tName.startsWith("secure") -> builder.secure(true)
            tName.startsWith("version") -> builder.version(tValue.toInt())
            tName.startsWith("httponly") -> builder.httpOnly(true)
            tName.startsWith("samesite") -> builder.sameSite(NewCookie.SameSite.valueOf(tValue.uppercase()))
            tName.startsWith("expires") -> builder.expiry(dateFormat.parse(tValue + ", " + (bites.next() as String).trim()))
          }
        }
      }

      return builder!!.build()
    }
  }
}