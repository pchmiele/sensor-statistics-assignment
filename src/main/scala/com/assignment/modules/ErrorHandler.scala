package com.assignment.modules

import com.assignment.domain.NoArgs
import zio.{UIO, ZLayer}
import zio.macros.accessible
import zio.console.Console

@accessible
object ErrorHandler {
  trait Service {
    def handleError(throwable: Throwable): UIO[Unit]
  }

  val live = ZLayer.fromService { (console: Console.Service) =>
    new Service {
      override def handleError(throwable: Throwable): UIO[Unit] = throwable match {
        case NoArgs => console.putStrLn("No arguments provided. Please provide path to directory with sensors data.")
        case failure => console.putStrLn(failure.getLocalizedMessage)
      }
    }
  }
}