package com.assignment.modules

import java.io.{File, FilenameFilter}
import java.nio.file.Path

import zio.blocking.Blocking
import zio.macros.accessible
import zio.stream.{ZStream, ZTransducer}
import zio.{IO, Task, ZLayer}

@accessible
object FileReader {
  trait Service {
    def linesStream(path: Path): ZStream[Blocking, Throwable, String]
    def listCsvFiles(path: String): Task[List[Path]]
  }

  val live = ZLayer.succeed(
    new Service {
      private val csvFilesFilter: FilenameFilter =
        (_: File, name: String) => name.toLowerCase.endsWith(".csv")

      override def linesStream(path: Path): ZStream[Blocking, Throwable, String] =
        ZStream
          .fromFile(path)
          .aggregate(ZTransducer.utf8Decode >>> ZTransducer.splitLines)

      // TODO: check when file for given path does not exist
      override def listCsvFiles(path: String): Task[List[Path]] = IO(
        Option(new File(path))
          .flatMap(file => Option(file.listFiles(csvFilesFilter)))
          .toList
          .flatten
          .map(_.toPath)
      )
    }
  )
}