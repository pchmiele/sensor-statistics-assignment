package com.assignment.modules

import java.nio.file.Path

import zio.ZLayer
import zio.blocking.Blocking
import zio.macros.accessible
import zio.stream.ZStream

@accessible
object SensorStatisticsProcessor {
  trait Service {
    def processMeasurementsFrom(filesWithMeasurements: Seq[Path]): ZStream[Blocking, Throwable, String]
  }

  val live = ZLayer.fromService { (fileReader: FileReader.Service) =>
    new Service {
      override def processMeasurementsFrom(filesWithMeasurements: Seq[Path]): ZStream[Blocking, Throwable, String] = {
        ZStream.mergeAllUnbounded()(filesWithMeasurements.map(fileReader.linesStream): _*)
      }
    }
  }
}