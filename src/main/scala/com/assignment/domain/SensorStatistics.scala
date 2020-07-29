package com.assignment.domain

case class SensorStatistics(
  min: Int,
  avg: Int,
  max: Int
)

case class SingleSensorStatisticsReport(
  sensorId: String,
  numOfProcessedMeasurements: Int,
  numOfFailedMeasurements: Int,
  sensorStatistics: Option[SensorStatistics]
) {
  def update(sensorMeasurement: SensorMeasurement): SingleSensorStatisticsReport = {
    (sensorMeasurement, sensorStatistics) match {
      case (SensorMeasurement(_, Some(humidity)), Some(SensorStatistics(min, avg, max))) =>
        val newMin = Math.min(min, humidity)
        val newMax = Math.max(max, humidity)
        val newAvg = (avg * numOfProcessedMeasurements + humidity) / (numOfProcessedMeasurements + 1)

        SingleSensorStatisticsReport(sensorId, numOfProcessedMeasurements + 1, numOfFailedMeasurements, Some(SensorStatistics(newMin, newAvg, newMax)))

      case (SensorMeasurement(_, Some(humidity)), None) =>
        SingleSensorStatisticsReport(sensorId, numOfProcessedMeasurements + 1, numOfFailedMeasurements, Some(SensorStatistics(humidity, humidity, humidity)))

      case (SensorMeasurement(_, None), Some(_)) =>
        SingleSensorStatisticsReport(sensorId, numOfProcessedMeasurements + 1, numOfFailedMeasurements + 1, sensorStatistics)

      case (SensorMeasurement(_, None), None) =>
        SingleSensorStatisticsReport(sensorId, numOfProcessedMeasurements + 1, numOfFailedMeasurements + 1, sensorStatistics)
    }
  }
}
object SingleSensorStatisticsReport {
  def apply(sensorId: String, sensorMeasurement: SensorMeasurement): SingleSensorStatisticsReport = {
    sensorMeasurement.humidity match {
      case Some(v) =>
        new SingleSensorStatisticsReport(sensorId = sensorId, numOfProcessedMeasurements = 1, numOfFailedMeasurements = 0, sensorStatistics = Some(SensorStatistics(v, v, v)))
      case None =>
        new SingleSensorStatisticsReport(sensorId = sensorId, numOfProcessedMeasurements = 1, numOfFailedMeasurements = 0, sensorStatistics = None)
    }
  }
}

case class MultipleSensorStatisticsReport(
  numOfProcessedFiles: Int,
  reports: Map[String, SingleSensorStatisticsReport]
)

object MultipleSensorStatisticsReport {
  def ofFiles(numOfFiles: Int): MultipleSensorStatisticsReport = MultipleSensorStatisticsReport(numOfFiles, Map.empty)
}