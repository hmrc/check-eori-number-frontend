import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val bootstrapVersion = "7.12.0"
  val playVersion = "play-28"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28"  % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc"          % "4.1.0-play-28",
    "uk.gov.hmrc"             %% "play-language"               % "6.0.0-play-28"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"      % bootstrapVersion        % Test,
    "org.scalatest"           %% "scalatest"                   % "3.2.9"                 % Test,
    "org.jsoup"               %  "jsoup"                       % "1.14.1"                % Test,
    "com.typesafe.play"       %% "play-test"                   % current                 % Test,
    "com.vladsch.flexmark"    %  "flexmark-all"                % "0.35.10"               % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"          % "5.0.0"                 % "test, it"
  )
}
