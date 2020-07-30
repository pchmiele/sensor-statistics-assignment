package com.assignment

import com.assignment.domain.{MultipleSensorStatisticsReport, SensorStatistics, SingleSensorStatisticsReport}
import com.assignment.modules.{ErrorHandler, FileReader, ReportRenderer, SensorStatisticsProcessor}
import zio.ZIO
import zio.blocking.Blocking
import zio.test.environment.{Live, TestConsole}
import zio.test.Assertion.equalTo
import zio.test.{DefaultRunnableSpec, TestResult, assertM, suite, testM}

object MainAppSpec extends DefaultRunnableSpec {
  private val testEnv =
    TestConsole.silent ++
      Blocking.live ++
      FileReader.live >+>
      SensorStatisticsProcessor.live ++
        ReportRenderer.live ++
        ErrorHandler.live

  private def checkProgramAgainstDataFromDir(dirName: String, expectedResult: MultipleSensorStatisticsReport): ZIO[Live with Any, Any, TestResult] = {
    val report = Main.program(List(s"src/it/resources/$dirName")).provideLayer(testEnv)
    assertM(report)(equalTo(expectedResult))
  }

  def spec = suite("Main")(
    testM("should correctly measure statistics from multiple valid files") {
      val expectedResult = MultipleSensorStatisticsReport(
        2,
        Map(
          "s1" -> SingleSensorStatisticsReport("s1", 3, 1, Some(new SensorStatistics(10, 54f, 98))),
          "s2" -> SingleSensorStatisticsReport("s2", 3, 0, Some(new SensorStatistics(78, 82f, 88))),
          "s3" -> SingleSensorStatisticsReport("s3", 1, 1, None))
      )
      checkProgramAgainstDataFromDir("multipleValidFiles", expectedResult)
    }
  )
}