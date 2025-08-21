import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val bootstrapVersion = "10.1.0"
  val playVersion      = "play-30"
  val mongoVersion     = "2.7.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-frontend-$playVersion" % bootstrapVersion,
    "uk.gov.hmrc"       %% s"play-frontend-hmrc-$playVersion" % "12.8.0",
    "uk.gov.hmrc.mongo" %% s"hmrc-mongo-$playVersion"         % mongoVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% s"bootstrap-test-$playVersion" % bootstrapVersion,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-30"      % mongoVersion,
    "org.scalatest"          %% "scalatest"                    % "3.2.19",
    "org.jsoup"               % "jsoup"                        % "1.18.3",
    "org.playframework"      %% "play-test"                    % current,
    "com.vladsch.flexmark"    % "flexmark-all"                 % "0.64.8",
    "org.scalatestplus.play" %% "scalatestplus-play"           % "7.0.1"
  ).map(_ % Test)
}
