name := "HiggsTwitter"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-sql" % "2.4.0",
    "org.apache.spark" %% "spark-graphx" % "2.4.0",
    "org.apache.spark" %% "spark-mllib" % "2.4.0",
    "org.graphstream" % "gs-core" % "1.3.1-SNAPSHOT",
    "org.graphstream" % "gs-algo" % "1.3.1-SNAPSHOT"
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"