name := "engine"

scalaVersion := "2.11.8"

val sparkVersion = "2.1.0"

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided"
libraryDependencies += ("org.apache.spark" %% "spark-streaming-kafka-0-8" % sparkVersion) exclude ("org.spark-project.spark", "unused")

assemblyJarName in assembly := name.value + ".jar"
