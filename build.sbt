addCommandAlias("build", ";createHeaders;compile;test;package")
addCommandAlias("rebuild", ";clean;build")

lazy val lomrf = Project("LoMRF", file("."))
  .enablePlugins(JavaAppPackaging, AutomateHeaderPlugin, sbtdocker.DockerPlugin)
	.settings(scalaVersion := "2.11.8")
	.settings(headers := LoMRFBuild.projectHeaders)
	.settings(logLevel in Test := Level.Info)
	.settings(logLevel in Compile := Level.Error)
	.settings(libraryDependencies ++= Dependencies.Akka)
	.settings(libraryDependencies ++= Dependencies.Logging)
	.settings(libraryDependencies ++= Dependencies.Utils)
	.settings(libraryDependencies ++= Dependencies.Optimus)
	.settings(libraryDependencies += Dependencies.ScalaTest)
