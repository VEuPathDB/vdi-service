package vdi.service.rest.util

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.TimeZone

private const val lenDay    = 10
private const val lenSecond = 19

val DateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

fun OffsetDateTime.defaultZone(): OffsetDateTime {
  return atZoneSameInstant(ZoneOffset.systemDefault()).toOffsetDateTime()
}

fun fixVariableDateString(date: String): OffsetDateTime? {
  val sb = StringBuilder(32)

  val ok = when (date.length) {
    lenDay    -> handleDay(sb, date)
    lenSecond -> handleSecond(sb, date)
    else      -> return null
  }

  if (!ok)
    return null

  return try {
    OffsetDateTime.parse(sb.toString(), DateTimeFormatter.ISO_DATE_TIME)
  } catch (_: Exception) {
    null
  }
}

private fun handleDay(sb: StringBuilder, date: String): Boolean {
  if (!(sb.writeYear(date) && sb.writeMonth(date) && sb.writeDay(date)))
    return false
  sb.append("T00:00:00.000")
  sb.writeZoneOffset()

  return true
}

private fun handleSecond(sb: StringBuilder, date: String): Boolean {
  if (!(sb.writeYear(date) && sb.writeMonth(date) && sb.writeDay(date) && sb.writeHour(date) && sb.writeMinute(date) && sb.writeSecond(date)))
    return false

  sb.append(".000")
  sb.writeZoneOffset()

  return true
}

// COMPONENT WRITERS ///////////////////////////////////////////////////////////

private fun StringBuilder.writeYear(date: String): Boolean {
  for (i in 0 until 4)
    if (isDecimalDigit(date[i]))
      append(date[i])
    else
      return false

  return true
}

private fun StringBuilder.writeMonth(date: String): Boolean {
  append('-')

  for (i in 5 until 7)
    if (isDecimalDigit(date[i]))
      append(date[i])
    else
      return false

  return true
}

private fun StringBuilder.writeDay(date: String): Boolean {
  append('-')

  for (i in 8 until 10)
    if (isDecimalDigit(date[i]))
      append(date[i])
    else
      return false

  return true
}

private fun StringBuilder.writeHour(date: String): Boolean {
  append('T')

  for (i in 11 until 13)
    if (isDecimalDigit(date[i]))
      append(date[i])
    else
      return false

  return true
}

private fun StringBuilder.writeMinute(date: String): Boolean {
  append(':')

  for (i in 14 until 16)
    if (isDecimalDigit(date[i]))
      append(date[i])
    else
      return false

  return true
}

private fun StringBuilder.writeSecond(date: String): Boolean {
  append(':')

  for (i in 17 until 19)
    if (isDecimalDigit(date[i]))
      append(date[i])
    else
      return false

  return true
}

fun StringBuilder.writeZoneOffset() {
  val hours = (TimeZone.getDefault().rawOffset/1000/60).toFloat()/60.0
  val minutes = hours % 1.0 * 60

  if (hours < 0) {
    append('-')
    if (hours > -10)
      append('0')
    append(-hours.toInt())
  } else {
    append('+')
    if (hours < 10)
      append('0')
    append(hours.toInt())
  }

  append(':')

  if (minutes < 10)
    append('0')

  append(minutes.toInt())
}

// MISC UTILS //////////////////////////////////////////////////////////////////

private fun isDecimalDigit(c: Char): Boolean {
  return c in '0'..'9'
}

