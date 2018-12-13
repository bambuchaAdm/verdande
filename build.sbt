name := "verdande"

version := "0.1.0-SNAPSHOT"

lazy val core = project in file("verdande-core")

lazy val jvm = project in file("verdande-jvm")

lazy val pushgateway = project in file("verdande-pushgateway")

lazy val play = project in file("verdande-play")

lazy val akka = project in file("verdande-akka")

lazy val akkaHttp = project in file("verdande-akka-http")

lazy val root = (project in file("."))
  .aggregate(core, jvm, pushgateway, play, akka, akkaHttp)
