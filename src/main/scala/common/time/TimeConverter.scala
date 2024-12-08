package aiven.kafka
package common.time

import java.time.format.DateTimeFormatter
import java.time.{ Instant, ZoneOffset, ZonedDateTime }

object TimeConverter {

  val DATE_FORMAT_ISO8601: String = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

  implicit class ApplyLong(val timestamp: Long) extends AnyVal {
    def format(fmt: String): String = {
      val instant   = Instant.ofEpochMilli(timestamp)
      val zdt       = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
      val formatter = DateTimeFormatter.ofPattern(fmt)
      formatter.format(zdt)
    }
  }
}
