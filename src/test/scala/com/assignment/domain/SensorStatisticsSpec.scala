package com.assignment.domain

import zio.test.Assertion._
import zio.test._
import zio.test.magnolia._

object SensorStatisticsSpec extends DefaultRunnableSpec {
  private val genMeasurement = DeriveGen[SensorMeasurement]
  private val genMeasurementWithHumidity = genMeasurement.filter(_.humidity.isDefined)
  private val genMeasurementNoHumidity = genMeasurement.map(_.copy(humidity = None))

  private val genReport = DeriveGen[SingleSensorStatisticsReport]
  private val genReportWithNoStatistics = genReport.map(_.copy(sensorStatistics = None))

  private def incrementBy1WhenFailed(measurement: SensorMeasurement): Int = measurement.humidity.map(_ => 0).getOrElse(1)

  def spec = suite("SensorStatisticsSpec")(
    testM("SingleSensorStatisticsReport.update should correctly update numOfFailedMeasurements") {
      check(genReport, genMeasurement) { (report, measurement) =>
        assert(report.update(measurement).numOfFailedMeasurements)(equalTo(report.numOfFailedMeasurements + incrementBy1WhenFailed(measurement)))
      }
    },
    testM("SingleSensorStatisticsReport.update should correctly update numOfProcessedMeasurements") {
      check(genReport, genMeasurement) { (report, measurement) =>
          assert(report.update(measurement).numOfProcessedMeasurements)(equalTo(report.numOfProcessedMeasurements + 1))
      }
    },
    testM("SingleSensorStatisticsReport.update should correctly update sensorStatistics when there is measured humidity and no statistics from sensor yet") {
      check(genReportWithNoStatistics, genMeasurementWithHumidity) { (report, measurement) =>
        assert(report.update(measurement).sensorStatistics)(equalTo(Some(SensorStatistics(measurement.humidity.get))))
      }
    },
    testM("SingleSensorStatisticsReport.update should correctly update sensorStatistics when there is no measured humidity") {
      check(genReport, genMeasurementNoHumidity) { (report, measurement) =>
        assert(report.update(measurement).sensorStatistics)(equalTo(report.sensorStatistics))
      }
    }
    ,
    test("SingleSensorStatisticsReport.update should correctly update sensorStatistics max value") {
      val report = SingleSensorStatisticsReport("sensorId", 1, 0, Some(SensorStatistics(1, 2, 3)))
      val measurement = SensorMeasurement("sensorId", Some(6))

      assert(report.update(measurement).sensorStatistics.get.max)(equalTo(6))
    },
    test("SingleSensorStatisticsReport.update should correctly update sensorStatistics min value") {
      val report = SingleSensorStatisticsReport("sensorId", 1, 0, Some(SensorStatistics(1, 2, 3)))
      val measurement = SensorMeasurement("sensorId", Some(0))

      assert(report.update(measurement).sensorStatistics.get.min)(equalTo(0))
    },
    test("SingleSensorStatisticsReport.update should correctly update sensorStatistics avg value") {
      val report = SingleSensorStatisticsReport("sensorId", 4, 2, Some(SensorStatistics(1, 8, 3)))
      val measurement = SensorMeasurement("sensorId", Some(8))

      assert(report.update(measurement).sensorStatistics.get.avg)(equalTo(8f))
    }
  )
}