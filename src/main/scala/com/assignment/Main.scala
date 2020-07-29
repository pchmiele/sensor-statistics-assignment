package com.assignment

import com.assignment.modules.{FileReader, SensorStatisticsProcessor}
import com.assignment.modules.FileReader._
import com.assignment.modules.SensorStatisticsProcessor._
import zio.blocking.Blocking
import zio.console.{putStrLn, _}
import zio.stream._
import zio.{App, IO}

object Main extends App{
  val env = Console.live ++ Blocking.live ++ FileReader.live  >+> SensorStatisticsProcessor.live
  def program(args: List[String]) = (for {
    csvFilePaths <- args.headOption
      .map(listCsvFiles)
      .getOrElse(IO.fail("No file specified"))
    measurements: ZStream[Blocking, Throwable, String] <- processMeasurementsFrom(csvFilePaths)
    results <- measurements.run(Sink.collectAll[String])
    _ <- putStrLn(results.mkString("\n"))
  } yield ())
    .onError(failure => putStrLn(failure.prettyPrint))


  override def run(args: List[String]) = {
    program(args).provideLayer(env).run.exitCode
  }
}
