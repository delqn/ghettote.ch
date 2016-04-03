name := """ghettotech"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _ )

libraryDependencies += "org.squeryl" % "squeryl_2.10" % "0.9.5-6"

libraryDependencies += "postgresql" % "postgresql" % "9.1-901-1.jdbc4"

libraryDependencies += "com.restfb" % "restfb" % "1.6.11"

libraryDependencies += "com.h2database" % "h2" % "1.3.167"

// play.Project.playScalaSettings
