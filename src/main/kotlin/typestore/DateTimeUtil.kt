package typestore

import com.google.cloud.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * [utcZoneId] is the [ZoneId] in UTC.
 */
val utcZoneId: ZoneId = ZoneId.of("UTC")

/**
 * [Timestamp.toLocalDateTime] returns the [LocalDateTime] version of this time.
 */
internal fun Timestamp.toLocalDateTime(): LocalDateTime = toSqlTimestamp().toLocalDateTime()

/**
 * [LocalDateTime.toGcpTimestamp] returns the GCP [Timestamp] version of this time.
 */
internal fun LocalDateTime.toGcpTimestamp(): Timestamp =
        Timestamp.of(java.sql.Timestamp.valueOf(this))

/**
 * [nowInUTC] returns the current time in UTC.
 */
fun nowInUTC(): LocalDateTime = LocalDateTime.now(utcZoneId)

/**
 * [ZonedDateTime.toUTCTime] converts this time to the local time in UTC.
 */
fun ZonedDateTime.toUTCTime(): LocalDateTime = withZoneSameInstant(utcZoneId).toLocalDateTime()

/**
 * [parseToUTCTime] returns the server's corresponding time with respect to user's supplied
 * [userTime] (without time zone) in ISO local date and [userTimezoneOffset].
 */
fun parseToUTCTime(userTime: String, userTimezoneOffset: Int): LocalDateTime {
    val userDate = LocalDateTime.parse(userTime, DateTimeFormatter.ISO_LOCAL_DATE)
    val userZoneId = ZoneId.ofOffset("UTC", ZoneOffset.ofHours(userTimezoneOffset))
    return userDate.atZone(userZoneId).toUTCTime()
}

/**
 * [toLocalDateTimeInUTC] returns the [LocalDateTime] in UTC that is equivalent to [date].
 */
fun toLocalDateTimeInUTC(date: Long): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(date), utcZoneId)

/**
 * [LocalDateTime.toUTCMillis] assumes this local date time is in UTC and converts it to a long that
 * represents the current time passed since 1970.
 */
fun LocalDateTime.toUTCMillis(): Long = toInstant(ZoneOffset.UTC).toEpochMilli()
