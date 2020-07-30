package com.assignment.domain

import com.assignment.domain.StringConstants.NaN

case class SensorStatistics(
  min: Int,
  avg: Float,
  max: Int
)
object SensorStatistics {
  def apply(value: Int): SensorStatistics = new SensorStatistics(value, value.toFloat, value)
}

case class SingleSensorStatisticsReport(
  sensorId: String,
  numOfProcessedMeasurements: Int,
  numOfFailedMeasurements: Int,
  sensorStatistics: Option[SensorStatistics]
) {
  def incrementNumOfProcessedMeasurements(): SingleSensorStatisticsReport =
    copy(numOfProcessedMeasurements = numOfProcessedMeasurements + 1)

  def incrementNumOfFailedMeasurements(): SingleSensorStatisticsReport =
    copy(numOfFailedMeasurements = numOfFailedMeasurements + 1)

  def updateSensorStatistics(sensorStatistics: SensorStatistics): SingleSensorStatisticsReport =
    copy(sensorStatistics = Some(sensorStatistics))

  def avg: Float =
    sensorStatistics.fold(Float.MinValue)(_.avg)

  def validMeasurementsCount = numOfProcessedMeasurements - numOfFailedMeasurements

  def update(sensorMeasurement: SensorMeasurement): SingleSensorStatisticsReport = {
    (sensorMeasurement, sensorStatistics) match {
      case (SensorMeasurement(_, Some(humidity)), Some(SensorStatistics(min, avg, max))) =>
        val newMin = Math.min(min, humidity)
        val newMax = Math.max(max, humidity)
        val newAvg = (avg * validMeasurementsCount + humidity) / (validMeasurementsCount + 1)

        incrementNumOfProcessedMeasurements()
          .updateSensorStatistics(SensorStatistics(newMin, newAvg, newMax))

      case (SensorMeasurement(_, Some(humidity)), None) =>
        incrementNumOfProcessedMeasurements()
          .updateSensorStatistics(SensorStatistics(humidity))

      case (SensorMeasurement(_, None), _) =>
        incrementNumOfProcessedMeasurements()
          .incrementNumOfFailedMeasurements()
    }
  }
}
object SingleSensorStatisticsReport {
  def apply(sensorId: String, sensorMeasurement: SensorMeasurement): SingleSensorStatisticsReport = {
    sensorMeasurement.humidity match {
      case Some(v) =>
        new SingleSensorStatisticsReport(
          sensorId = sensorId,
          numOfProcessedMeasurements = 1,
          numOfFailedMeasurements = 0,
          sensorStatistics = Some(SensorStatistics(v))
        )

      case None =>
        new SingleSensorStatisticsReport(
          sensorId = sensorId,
          numOfProcessedMeasurements = 1,
          numOfFailedMeasurements = 1,
          sensorStatistics = None
        )
    }
  }
}

case class MultipleSensorStatisticsReport(
  numOfProcessedFiles: Int,
  reports: Map[String, SingleSensorStatisticsReport]
) {
  def numOfProcessedMeasurements: Int = reports.values.map(_.numOfProcessedMeasurements).sum
  def numOfFailedMeasurements: Int = reports.values.map(_.numOfFailedMeasurements).sum
  def sensorStatistics: Seq[(String, String, String, String)] = {
    reports
      .toSeq
      .sortBy{ case (_, value) => value.avg }(Ordering[Float].reverse)
      .map { case (key, sensor) =>
        val min  = sensor.sensorStatistics.map(_.min.toString).getOrElse(NaN)
        val avg = sensor.sensorStatistics.map(_.avg.toString).getOrElse(NaN)
        val max = sensor.sensorStatistics.map(_.max.toString).getOrElse(NaN)
        (key,min,avg,max)
      }
  }
}
object MultipleSensorStatisticsReport {
  def ofFiles(numOfFiles: Int): MultipleSensorStatisticsReport = MultipleSensorStatisticsReport(numOfFiles, Map.empty)
}