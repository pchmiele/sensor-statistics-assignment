package com.assignment

import com.assignment.domain.{MultipleSensorStatisticsReport, NoArgs, SingleSensorStatisticsReport}
import com.assignment.modules.FileReader._
import com.assignment.modules.ReportRenderer._
import com.assignment.modules.SensorStatisticsProcessor._
import com.assignment.modules.{FileReader, ReportRenderer, SensorStatisticsProcessor}
import zio.blocking.Blocking
import zio.console.{putStrLn, _}
import zio.stream._
import zio.{App, IO}

object Main extends App {
  val env = Console.live ++ Blocking.live ++ FileReader.live >+> SensorStatisticsProcessor.live ++ ReportRenderer.live

  def program(args: List[String]) = {
    (for {
      dir <- IO.fromOption(args.headOption).mapError(_ => NoArgs)
      csvFilePaths <- listCsvFiles(dir)
      measurements <- processMeasurementsFrom(csvFilePaths)
      report <- measurements.run(Sink.foldLeft(MultipleSensorStatisticsReport.ofFiles(csvFilePaths.length)) {
        case (MultipleSensorStatisticsReport(n, reports), Some(sensorMeasurement)) =>
          val updatedReport = reports.get(sensorMeasurement.sensorId).map {
            _.update(sensorMeasurement)
          }.getOrElse(SingleSensorStatisticsReport(sensorMeasurement.sensorId, sensorMeasurement))
          MultipleSensorStatisticsReport(n, reports + (sensorMeasurement.sensorId -> updatedReport))
        case (report, None) => report
      })
      _ <- renderReport(report)
    } yield ()
    ).catchSome {
      case NoArgs => putStrLn("No arguments provided")
      case failure => putStrLn(failure.getLocalizedMessage)
    }
  }

  override def run(args: List[String]) = {
    program(args).provideLayer(env).run.exitCode
  }
}
