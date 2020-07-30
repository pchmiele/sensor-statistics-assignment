import sbt._

object Deps {
  val zioVersion = "1.0.0-RC21-2"
  val dependencies = Seq(
    "dev.zio"       %% "zio"                  % zioVersion,
    "dev.zio"       %% "zio-macros"           % zioVersion,
    "dev.zio"       %% "zio-streams"          % zioVersion,
    "dev.zio"       %% "zio-test-sbt"         % zioVersion       % "test",
    "dev.zio"       %% "zio-test-magnolia"    % zioVersion       % "test"
  )
}