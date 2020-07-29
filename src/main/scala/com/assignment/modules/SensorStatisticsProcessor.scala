package com.assignment.modules

import java.nio.file.Path

import com.assignment.domain.SensorMeasurement
import com.assignment.modules.StringConstants.{NaN, comma}
import zio.ZLayer
import zio.blocking.Blocking
import zio.macros.accessible
import zio.stream.ZStream

object StringConstants {
  val NaN = "NaN"
  val comma = ","
}

@accessible
object SensorStatisticsProcessor {
  trait Service {
    def processMeasurementsFrom(filesWithMeasurements: Seq[Path]): ZStream[Blocking, Throwable, Option[SensorMeasurement]]
  }

  val live = ZLayer.fromService { (fileReader: FileReader.Service) =>
    new Service {
      //TODO: check if there is no ,
      //TODO: check if there is not valid int as second param
      private def lineToMeasurement(line: String): Option[SensorMeasurement] = {
        line.split(comma).toList match {
          case sensorId :: NaN  :: _ => Some(SensorMeasurement(sensorId, None))
          case sensorId :: humidity :: _ if humidity.toIntOption.isDefined => Some(SensorMeasurement(sensorId, Some(humidity.toInt)))
          case _ => None
        }
      }

      private def processSingleFileMeasurement(path: Path): ZStream[Blocking, Throwable, Option[SensorMeasurement]] = {
        fileReader
          .linesStream(path)
          .drop(1)
          .map(lineToMeasurement)
      }

      override def processMeasurementsFrom(filesWithMeasurements: Seq[Path]): ZStream[Blocking, Throwable, Option[SensorMeasurement]] = {
        ZStream.mergeAllUnbounded()(filesWithMeasurements.map(processSingleFileMeasurement): _*)
      }
    }
  }
}