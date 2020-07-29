import sbt._

object Deps {
  val catsVersion = "2.0.0"
  val fs2Version = "2.0.0"
  val scalaTestVersion = "3.2.0"
  val zioVersion = "1.0.0-RC21-2"
  val dependencies = Seq(
    "org.typelevel" %% "cats-core"            % catsVersion,
    "org.typelevel" %% "cats-effect"          % catsVersion,
    "co.fs2"        %% "fs2-core"             % fs2Version,
    "co.fs2"        %% "fs2-io"               % fs2Version,
    "dev.zio"       %% "zio"                  % zioVersion,
    "dev.zio"       %% "zio-macros"           % zioVersion,
    "dev.zio"       %% "zio-streams"          % zioVersion,
    "dev.zio"       %% "zio-test-sbt"         % zioVersion       % "test",
    "dev.zio"       %% "zio-test-magnolia"    % zioVersion       % "test",
    "org.scalatest" %% "scalatest"            % scalaTestVersion % "test"
  )
}