// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.3")


// from https://raw.githubusercontent.com/hellschreiber/skeleton/master/project/plugins.sbt

resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "3.0.0")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.4")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.6.0")
