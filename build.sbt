organization := "com.feth"

name := "play-authenticate-usage"

scalaVersion := "2.11.6"

version := "1.0-SNAPSHOT"

val appDependencies = Seq(
  cache,
  javaWs,
  javaJpa,
  "org.webjars" % "bootstrap" % "3.2.0",
  "org.easytesting" % "fest-assert" % "1.4" % "test",
  "mysql" % "mysql-connector-java" % "5.1.36",
  "net.coobird" % "thumbnailator" % "0.4.7",
   "be.objectify"  %% "deadbolt-java"     % "2.4.0",
  "org.hibernate" % "hibernate-entitymanager" % "4.3.9.Final",
  "commons-pool" % "commons-pool" % "1.6",
  "commons-lang" % "commons-lang" % "2.6",
  "commons-collections" % "commons-collections" % "3.2.1",
  "commons-io" % "commons-io" % "2.4",
  "joda-time" % "joda-time" % "2.3",
  "com.google.guava" % "guava" % "12.0",
  "com.ganyo" % "gcm-server" % "1.0.2",
  "com.typesafe.play.modules" %% "play-modules-redis" % "2.4.1",
  "org.apache.httpcomponents" % "httpclient" % "4.5",
  "com.feth" %% "play-easymail" % "0.7.0",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "com.typesafe.play" %% "play-mailer" % "3.0.0-M1",
  "org.apache.httpcomponents" % "httpasyncclient" % "4.0-beta4",
  "org.apache.httpcomponents" % "httpclient" % "4.3",
  "org.apache.httpcomponents" % "httpmime" % "4.3",
  "org.json" % "json" % "20090211",
  "com.mashape.unirest" % "unirest-java" % "1.3.0"
  
)

// add resolver for deadbolt and easymail snapshots


// display deprecated or poorly formed Java
javacOptions ++= Seq("-Xlint:unchecked")
javacOptions ++= Seq("-Xlint:deprecation")
javacOptions ++= Seq("-Xdiags:verbose")

//  Uncomment the next line for local development of the Play Authenticate core:
//lazy val playAuthenticate = project.in(file("modules/play-authenticate")).enablePlugins(PlayJava)

PlayKeys.externalizeResources := false

lazy val root = project.in(file("."))
  .enablePlugins(PlayJava)
  .settings(
    libraryDependencies ++= appDependencies
  )
  .settings(
  	resolvers += Resolver.sonatypeRepo("snapshots"),
  	resolvers += "google-sedis-fix" at "http://pk11-scratch.googlecode.com/svn/trunk"
  )
  /* Uncomment the next lines for local development of the Play Authenticate core: */
  //.dependsOn(playAuthenticate)
  //.aggregate(playAuthenticate)
