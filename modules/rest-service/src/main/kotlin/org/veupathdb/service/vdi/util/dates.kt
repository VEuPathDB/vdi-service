package org.veupathdb.service.vdi.util

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.TimeZone

private const val lenYear   = 4
private const val lenMonth  = 7
private const val lenDay    = 10
private const val lenHour   = 13
private const val lenMinute = 16
private const val lenSecond = 19
private const val lenMillis = 23
private const val lenUTC    = 24
private const val lenZoned  = 29

fun OffsetDateTime.defaultZone(): OffsetDateTime {
  return atZoneSameInstant(ZoneOffset.systemDefault()).toOffsetDateTime()
}

fun fixVariableDateString(date: String, fn: () -> Exception = ::Exception): OffsetDateTime {
  val sb = StringBuilder(32)

  when (date.length) {
    lenYear   -> handleYear(sb, date, fn)
    lenMonth  -> handleMonth(sb, date, fn)
    lenDay    -> handleDay(sb, date, fn)
    lenHour   -> handleHour(sb, date, fn)
    lenMinute -> handleMinute(sb, date, fn)
    lenSecond -> handleSecond(sb, date, fn)
    lenMillis -> handleMillis(sb, date, fn)
    lenUTC    -> handleUTC(sb, date, fn)
    lenZoned  -> handleZoned(sb, date, fn)
    else      -> throw fn()
  }

  return try {
    OffsetDateTime.parse(sb.toString(), DateTimeFormatter.ISO_DATE_TIME)
  } catch (_: Exception) {
    throw fn()
  }
}

private fun handleYear(sb: StringBuilder, date: String, fn: () -> Exception) {
  sb.writeYear(date, fn)
  sb.append("-01-01T00:00:00.000")
  sb.writeZoneOffset()
}

private fun handleMonth(sb: StringBuilder, date: String, fn: () -> Exception) {
  sb.writeYear(date, fn)
  sb.writeMonth(date, fn)
  sb.append("-01T00:00:00.000")
  sb.writeZoneOffset()
}

private fun handleDay(sb: StringBuilder, date: String, fn: () -> Exception) {
  sb.writeYear(date, fn)
  sb.writeMonth(date, fn)
  sb.writeDay(date, fn)
  sb.append("T00:00:00.000")
  sb.writeZoneOffset()
}

private fun handleHour(sb: StringBuilder, date: String, fn: () -> Exception) {
  sb.writeYear(date, fn)
  sb.writeMonth(date, fn)
  sb.writeDay(date, fn)
  sb.writeHour(date, fn)
  sb.append(":00:00.000")
  sb.writeZoneOffset()
}

private fun handleMinute(sb: StringBuilder, date: String, fn: () -> Exception) {
  sb.writeYear(date, fn)
  sb.writeMonth(date, fn)
  sb.writeDay(date, fn)
  sb.writeHour(date, fn)
  sb.writeMinute(date, fn)
  sb.append(":00.000")
  sb.writeZoneOffset()
}

private fun handleSecond(sb: StringBuilder, date: String, fn: () -> Exception) {
  sb.writeYear(date, fn)
  sb.writeMonth(date, fn)
  sb.writeDay(date, fn)
  sb.writeHour(date, fn)
  sb.writeMinute(date, fn)
  sb.writeSecond(date, fn)
  sb.append(".000")
  sb.writeZoneOffset()
}

private fun handleMillis(sb: StringBuilder, date: String, fn: () -> Exception) {
  sb.writeYear(date, fn)
  sb.writeMonth(date, fn)
  sb.writeDay(date, fn)
  sb.writeHour(date, fn)
  sb.writeMinute(date, fn)
  sb.writeSecond(date, fn)
  sb.writeMillis(date, fn)
  sb.writeZoneOffset()
}

private fun handleUTC(sb: StringBuilder, date: String, fn: () -> Exception) {
  sb.writeYear(date, fn)
  sb.writeMonth(date, fn)
  sb.writeDay(date, fn)
  sb.writeHour(date, fn)
  sb.writeMinute(date, fn)
  sb.writeSecond(date, fn)
  sb.writeMillis(date, fn)

  if (date[23] == 'z')
    sb.append('Z')
  else if (date[23] == 'Z')
    sb.append('Z')
  else
    throw fn()
}

private fun handleZoned(sb: StringBuilder, date: String, fn: () -> Exception) {
  sb.writeYear(date, fn)
  sb.writeMonth(date, fn)
  sb.writeDay(date, fn)
  sb.writeHour(date, fn)
  sb.writeMinute(date, fn)
  sb.writeSecond(date, fn)
  sb.writeMillis(date, fn)

  if (date[23] == '-' || date[23] == '+')
    sb.append(date[23])
  else
    throw fn()

  for (i in 24 until 26)
    if (isDecimalDigit(date[i]))
      sb.append(date[i])
    else
      throw fn()

  if (date[26] == ':')
    sb.append(':')
  else
    throw fn()

  for (i in 27 until 29)
    if (isDecimalDigit(date[i]))
      sb.append(date[i])
    else
      throw fn()
}

// COMPONENT WRITERS ///////////////////////////////////////////////////////////

private fun StringBuilder.writeYear(date: String, fn: () -> Exception) {
  for (i in 0 until 4)
    if (isDecimalDigit(date[i]))
      append(date[i])
    else
      throw fn()
}

private fun StringBuilder.writeMonth(date: String, fn: () -> Exception) {
  append('-')

  for (i in 5 until 7)
    if (isDecimalDigit(date[i]))
      append(date[i])
    else
      throw fn()
}

private fun StringBuilder.writeDay(date: String, fn: () -> Exception) {
  append('-')

  for (i in 8 until 10)
    if (isDecimalDigit(date[i]))
      append(date[i])
    else
      throw fn()
}

private fun StringBuilder.writeHour(date: String, fn: () -> Exception) {
  append('T')

  for (i in 11 until 13)
    if (isDecimalDigit(date[i]))
      append(date[i])
    else
      throw fn()
}

private fun StringBuilder.writeMinute(date: String, fn: () -> Exception) {
  append(':')

  for (i in 14 until 16)
    if (isDecimalDigit(date[i]))
      append(date[i])
    else
      throw fn()
}

private fun StringBuilder.writeSecond(date: String, fn: () -> Exception) {
  append(':')

  for (i in 17 until 19)
    if (isDecimalDigit(date[i]))
      append(date[i])
    else
      throw fn()
}

private fun StringBuilder.writeMillis(date: String, fn: () -> Exception) {
  append('.')

  for (i in 20 until 23)
    if (isDecimalDigit(date[i]))
      append(date[i])
    else
      throw fn()
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

