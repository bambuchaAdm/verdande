name := "verdande"

version := "0.1.0-SNAPSHOT"

lazy val commonSettings = Seq(
  scalaVersion := "2.12.8",
  crossScalaVersions := List("2.11.12", "2.12.8"),
  parallelExecution in Test := false // https://stackoverflow.com/a/49024536
)

lazy val core = (project in file("verdande-core"))
  .settings(commonSettings)

lazy val jvm = (project in file("verdande-jvm"))
  .settings(commonSettings)

lazy val pushgateway = (project in file("verdande-pushgateway"))
  .settings(commonSettings)

lazy val play = (project in file("verdande-play"))
  .settings(commonSettings)

lazy val akka = (project in file("verdande-akka"))
  .settings(commonSettings)

lazy val akkaHttp = (project in file("verdande-akka-http"))
  .settings(commonSettings)

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    (scalafmtConfig in ThisBuild) := Some(baseDirectory.value / "project" / "scalafmt.conf")
  )
  .aggregate(core, jvm, pushgateway, play, akka, akkaHttp)
