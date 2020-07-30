package com.assignment

import com.assignment.modules.{ErrorHandler, FileReader, ReportRenderer, SensorStatisticsProcessor}
import zio.blocking.Blocking
import zio.console.Console

object Environments {
  val appEnv = Console.live ++ Blocking.live ++ FileReader.live >+> SensorStatisticsProcessor.live ++ ReportRenderer.live ++ ErrorHandler.live
}
