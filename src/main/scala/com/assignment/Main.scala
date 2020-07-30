package com.assignment

import com.assignment.domain.NoArgs
import com.assignment.modules.ErrorHandler._
import com.assignment.modules.FileReader._
import com.assignment.modules.ReportRenderer._
import com.assignment.modules.SensorStatisticsProcessor._
import com.assignment.modules.{ErrorHandler, FileReader, ReportRenderer, SensorStatisticsProcessor}
import zio.blocking.Blocking
import zio.console._
import zio.{App, IO}

object Main extends App {
  val env = Console.live ++ Blocking.live ++ FileReader.live >+> SensorStatisticsProcessor.live ++ ReportRenderer.live ++ ErrorHandler.live

  def program(args: List[String]) =
    for {
      dir <- IO.fromOption(args.headOption).mapError(_ => NoArgs)
      csvFilePaths <- listCsvFiles(dir)
      measurements <- processMeasurementsFrom(csvFilePaths)
      report <- combineMeasurements(csvFilePaths.length, measurements)
      _ <- renderReport(report)
    } yield ()

  override def run(args: List[String]) = {
    program(args)
      .catchSome(handleError(_))
      .provideLayer(env)
      .run
      .exitCode
  }
}
