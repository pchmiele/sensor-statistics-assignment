package com.assignment.modules

import java.nio.file.Path

import com.assignment.domain.StringConstants.{NaN, comma}
import com.assignment.domain.{MeasurementsStream, MultipleSensorStatisticsReport, SensorMeasurement, SingleSensorStatisticsReport}
import zio.blocking.Blocking
import zio.macros.accessible
import zio.stream.{Sink, ZStream}
import zio.{ZIO, ZLayer}

@accessible
object SensorStatisticsProcessor {
  trait Service {
    def processMeasurementsFrom(filesWithMeasurements: Seq[Path]): MeasurementsStream
    def combineMeasurements(numOfFiles: Int, measurements: MeasurementsStream): ZIO[Blocking, Throwable, MultipleSensorStatisticsReport]
  }

  val live = ZLayer.fromService { (fileReader: FileReader.Service) =>
    new Service {

      private def lineToMeasurement(line: String): Option[SensorMeasurement] = {
        line.split(comma).toList match {
          case sensorId :: NaN  :: _ => Some(SensorMeasurement(sensorId, None))
          case sensorId :: humidity :: _ if humidity.toIntOption.isDefined => Some(SensorMeasurement(sensorId, Some(humidity.toInt)))
          case _ => None
        }
      }

      private def processSingleFileMeasurement(path: Path): MeasurementsStream = {
        fileReader
          .linesStream(path)
          .drop(1)
          .map(lineToMeasurement)
      }

      override def processMeasurementsFrom(filesWithMeasurements: Seq[Path]): MeasurementsStream = {
        ZStream.mergeAllUnbounded()(filesWithMeasurements.map(processSingleFileMeasurement): _*)
      }

      override def combineMeasurements(numOfFiles: Int, measurements: MeasurementsStream): ZIO[Blocking, Throwable, MultipleSensorStatisticsReport] = {
        measurements.run(Sink.foldLeft(MultipleSensorStatisticsReport.ofFiles(numOfFiles)) {
          case (MultipleSensorStatisticsReport(n, reports), Some(sensorMeasurement)) =>
            val updatedReport = reports.get(sensorMeasurement.sensorId).map {
              _.update(sensorMeasurement)
            }.getOrElse(SingleSensorStatisticsReport(sensorMeasurement.sensorId, sensorMeasurement))
            MultipleSensorStatisticsReport(n, reports + (sensorMeasurement.sensorId -> updatedReport))
          case (report, None) => report
        })
      }
    }
  }
}