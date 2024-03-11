import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val bootstrapVersion = "8.4.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc" %% "play-frontend-hmrc-play-30"         % bootstrapVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30" % bootstrapVersion ,
    "org.scalatest"          %% "scalatest"              % "3.2.15"        ,
    "org.jsoup"               % "jsoup"                  % "1.15.4"         ,
    "org.playframework"      %% "play-test"              % current         ,
    "com.vladsch.flexmark"    % "flexmark-all"           % "0.64.6"        ,
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0"
  ).map(_ % Test)
}
