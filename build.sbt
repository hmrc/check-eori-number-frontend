import uk.gov.hmrc.DefaultBuildSettings

scalafmtOnCompile := true

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.checkeorinumberfrontend.config.AppConfig",
      "uk.gov.hmrc.checkeorinumberfrontend.views.html._",
      "uk.gov.hmrc.checkeorinumberfrontend.views.html.components._",
      "uk.gov.hmrc.checkeorinumberfrontend.views.html.helpers._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._"
    ),
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=routes/.*:s"
  )
  .settings(resolvers += Resolver.jcenterRepo)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(
    microservice % "test->test"
  ) // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.test)

PlayKeys.playDefaultPort := 8350
val appName = "check-eori-number-frontend"
