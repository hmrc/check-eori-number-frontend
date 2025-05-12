import uk.gov.hmrc.DefaultBuildSettings

scalafmtOnCompile := true

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.16"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala,SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
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
    // ***************
    // Use the silencer plugin to suppress warnings
    // You may turn it on for `views` too to suppress warnings from unused imports in compiled twirl templates, but this will hide other warnings.
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=routes/.*:s",
    scalacOptions += "-P:silencer:pathFilters=routes",
    scoverageSettings,
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
    // ***************
  )
  .settings(resolvers += Resolver.jcenterRepo)

PlayKeys.playDefaultPort := 8350
val appName         = "check-eori-number-frontend"
val silencerVersion = "1.7.19"

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.test)

lazy val scoverageSettings: Seq[Setting[_]] = Seq(
  coverageExcludedPackages := "<empty>;Reverse.*;.*config.ErrorHandler;.*components.*;" +
    ".*javascript.*;.*Routes.*;.*viewmodels.*;.*ViewUtils.*;.*GuiceInjector;.*views.*;" +
    ".*Routes.*;.*viewmodels.govuk.*;app.*;prod.*",
  coverageMinimumStmtTotal := 97,
  coverageFailOnMinimum    := false,
  coverageHighlighting     := true,
  Test / parallelExecution := false
)
