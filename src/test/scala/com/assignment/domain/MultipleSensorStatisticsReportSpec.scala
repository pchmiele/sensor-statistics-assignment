package com.assignment.domain

import zio.test.Assertion._
import zio.test._

object MultipleSensorStatisticsReportSpec extends DefaultRunnableSpec {
  private val report = MultipleSensorStatisticsReport(
    10,
    Map(
      "sensorId1" -> new SingleSensorStatisticsReport("sensorId1", 5, 3, Some(new SensorStatistics(1, 3, 4))),
      "sensorId2" -> new SingleSensorStatisticsReport("sensorId2", 5, 3, Some(new SensorStatistics(1, 5, 10))),
      "sensorId3" -> new SingleSensorStatisticsReport("sensorId3", 5, 3, Some(new SensorStatistics(1, 2, 3))),
      "sensorId4" -> new SingleSensorStatisticsReport("sensorId4", 5, 3, None),
    )
  )

  def spec = suite("MultipleSensorStatisticsReport")(
    test("numOfFailedMeasurements correctly calculate number of failed measurements") {
      assert(report.numOfFailedMeasurements)(equalTo(12))
    },
    test("numOfProcessedMeasurements correctly calculate number of processed measurements") {
      assert(report.numOfProcessedMeasurements)(equalTo(20))
    },
    test("sensorStatistics correctly lists correctly sensor statistics (sorted by avg)") {
      assert(report.sensorStatistics)(
        hasSameElements(
          Seq(
            ("sensorId2", "1", "5", "10"),
            ("sensorId1", "1", "3", "4"),
            ("sensorId3", "1", "2", "3"),
            ("sensorId4", "NaN", "NaN", "NaN")
          )
        )
      )
    }
  )
}