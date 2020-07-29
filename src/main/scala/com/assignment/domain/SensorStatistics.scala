package com.assignment.domain

case class SensorStatistics(
  sensorId: String,
  min: Int,
  avg: Int,
  max: Int
)

case class SingleFileSensorStatisticsReport(
  numOfProcessedMeasurements: Int,
  numOfFailedMeasurements: Int,
  sensorStatistics: List[SensorStatistics]
)

case class MultipleSensorStatisticsReport(
  numOfProcessedFiles: Int,
  numOfProcessedMeasurements: Int,
  numOfFailedMeasurements: Int,
  sensorStatistics: List[SensorStatistics]
)