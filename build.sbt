name := "tzscala"

version := "0.1"

scalaVersion := "2.11.8"

resolvers += "aliyun" at "http://maven.aliyun.com/nexus/content/groups/public/"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.0.0" % "provided",
  "org.apache.spark" %% "spark-sql" % "2.0.0" % "provided",
  "org.apache.spark" %% "spark-mllib" % "2.0.0" % "provided",
  "org.apache.spark" %% "spark-hive" % "2.0.0" % "provided",
  "org.apache.logging.log4j" % "log4j-api" % "2.10.0" ,
  "org.apache.logging.log4j" % "log4j-core" % "2.10.0" ,
  "com.alibaba" % "fastjson" % "1.2.42",
  "org.elasticsearch" % "elasticsearch" % "2.3.1",
  "org.elasticsearch" % "elasticsearch-hadoop" % "6.0.1" excludeAll(
    ExclusionRule(organization = "org.apache.hive"),
    ExclusionRule(organization = "org.apache.pig"),
    ExclusionRule(organization = "org.apache.storm"),
    ExclusionRule("cascading","cascading-hadoop"),
    ExclusionRule("cascading","cascading-local"),
    ExclusionRule("org.pentaho","pentaho-aggdesigner-algorithm")
  )
)

javacOptions ++= Seq("-encoding", "UTF-8")

assemblyMergeStrategy in assembly := {
  case PathList("org", "joda", "time", xs @ _*) =>
    MergeStrategy.last
  case PathList("mime.types") =>
    MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)