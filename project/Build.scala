import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "bb-server"

  //val appVersion      = "1.0-SNAPSHOT"
  val appVersion      = "%s-%s".format("git rev-parse --abbrev-ref HEAD".!!.trim, "git rev-parse --short HEAD".!!.trim)

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaJpa,
    "mysql" % "mysql-connector-java" % "5.1.36",
    "org.hibernate" % "hibernate-entitymanager" % "4.2.7.Final",
    "org.hamcrest" % "hamcrest-all" % "1.3",
    "joda-time" % "joda-time" % "2.3",
    "com.google.guava" % "guava" % "12.0",
    "be.objectify"  %%  "deadbolt-java" % "2.1-RC2",
    "com.feth" %% "play-easymail" % "0.3-SNAPSHOT",
    "org.mindrot" % "jbcrypt" % "0.3m",
    "org.apache.httpcomponents" % "httpclient" % "4.2.5",
    "net.coobird" % "thumbnailator" % "0.4.7",
    "com.clever-age" % "play2-elasticsearch" % "0.5.5",
    "commons-pool" % "commons-pool" % "1.6",
    "commons-lang" % "commons-lang" % "2.6",
    "commons-collections" % "commons-collections" % "3.2.1",
    "commons-io" % "commons-io" % "2.4",
    "biz.source_code" % "base64coder" % "2010-12-19",
    "redis.clients" % "jedis" % "2.2.1",
    "org.sedis" % "sedis" % "1.0.1",
    "org.jsoup" % "jsoup" % "1.7.3"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
      Keys.fork in (Test) := false,
      testOptions in Test ~= { args =>
       for {
        arg <- args
        val ta: Tests.Argument = arg.asInstanceOf[Tests.Argument]
        val newArg = if(ta.framework == Some(TestFrameworks.JUnit)) ta.copy(args = List.empty[String]) else ta
       } yield newArg
      }, 
      resolvers += Resolver.url("Objectify Play Repository (release)", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("Objectify Play Repository (snapshot)", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
  	  resolvers += Resolver.url("play-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
  )

}
