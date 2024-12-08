package aiven.kafka
package common.time

import munit.FunSuite
import TimeConverter.*

class TimeConverterSpec extends FunSuite {

  test("convert long timestamp to a given format") {
    assertEquals(1674375135000L.format(DATE_FORMAT_ISO8601), "2023-01-22T08:12:15.000+0000")
  }
}
