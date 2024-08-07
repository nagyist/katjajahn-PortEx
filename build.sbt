name := "PortEx"

version := "5.0.5-SNAPSHOT"

scalaVersion := "2.12.13"

assembly / assemblyJarName := "PortexAnalyzer.jar"

assembly / mainClass := Some("com.github.struppigel.tools.PortExAnalyzer")

lazy val JavaDoc = config("genjavadoc") extend Compile

lazy val javadocSettings = inConfig(JavaDoc)(Defaults.configSettings) ++ Seq(
  addCompilerPlugin("com.typesafe.genjavadoc" %% "genjavadoc-plugin" % "0.18_2.13.10" cross CrossVersion.full),
  scalacOptions += s"-P:genjavadoc:out=${target.value}/java",
  Compile / packageDoc := (JavaDoc / packageDoc).value,
  JavaDoc / sources :=
    (target.value / "java" ** "*.java").get ++
      (Compile / sources).value.filter(_.getName.endsWith(".java")),
  JavaDoc / javacOptions := Seq("-Xdoclint:none"),
  JavaDoc / packageDoc / artifactName := ((sv, mod, art) =>
    "" + mod.name + "_" + sv.binary + "-" + mod.revision + "-javadoc.jar")
)

lazy val root = project.in(file(".")).configs(JavaDoc).settings(javadocSettings: _*)

libraryDependencies += "org.testng" % "testng" % "6.14.3" % "test"

libraryDependencies += "com.google.guava" % "guava" % "31.1-jre"

libraryDependencies += "com.google.code.findbugs" % "jsr305" % "3.0.2"

libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.23.1"

libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.23.1"

// Your project organization (package name)
organization := "com.github.struppigel"

pomIncludeRepository := { _ => false }

scmInfo := Some(
  ScmInfo(
    url("https://github.com/struppigel/PortEx.git"),
    "scm:git@github.com:struppigel/PortEx.git"
  )
)

developers := List(
  Developer(
    id    = "struppigel",
    name  = "Karsten Hahn",
    email = "struppigel@googlemail.com",
    url   = url("https://github.com/struppigel/PortEx")
  )
)

licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))

description := "Java library to parse Portable Executable files"

homepage := Some(url("https://github.com/struppigel/PortEx"))

githubOwner := "struppigel"

githubRepository := "PortEx"

publishTo := Some("GitHub struppigel Apache Maven Packages" at "https://maven.pkg.github.com/struppigel/PortEx")

versionScheme := Some("early-semver") // meaning: major.minor.patch version scheme

publishMavenStyle := true

credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  "struppigel",
  System.getenv("GITHUB_TOKEN")
)

Global / onChangedBuildSource := ReloadOnSourceChanges