package com.assignment.modules

import com.assignment.domain.MultipleSensorStatisticsReport
import com.assignment.domain.StringConstants.newLine
import zio.console.Console
import zio.macros.accessible
import zio.{UIO, ZIO, ZLayer}

@accessible
object ReportRenderer {
  trait Service {
    def renderReport(report: MultipleSensorStatisticsReport): UIO[Unit]
  }

  val live = ZLayer.fromService { (console: Console.Service) =>
    new Service {
      private def renderSortedSensors(report: MultipleSensorStatisticsReport) = {
        if(report.sensorStatistics.isEmpty) {
          ZIO.unit
        } else {
          for {
            _ <- console.putStrLn("")
            _ <- console.putStrLn("Sensors with highest avg humidity:")
            _ <- console.putStrLn("")
            _ <- console.putStrLn("sensor-id,min,avg,max")
            sensors = report.sensorStatistics.map {
              case (key,min,avg,max) => s"$key,$min,$avg,$max"
            }
            _ <- console.putStrLn(s"${sensors.mkString(newLine)}")
          } yield ()
        }
      }

      override def renderReport(report: MultipleSensorStatisticsReport): UIO[Unit] = {
        for {
          _ <- console.putStrLn(s"Num of processed files: ${report.numOfProcessedFiles}")
          _ <- console.putStrLn(s"Num of processed measurements: ${report.numOfProcessedMeasurements}")
          _ <- console.putStrLn(s"Num of failed measurements: ${report.numOfFailedMeasurements}")
          _ <- renderSortedSensors(report)
        } yield ()
      }
    }
  }
}