name := "engine"

scalaVersion := "2.11.8"

val sparkVersion = "1.6.2"

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided"
libraryDependencies += ("org.apache.spark" %% "spark-streaming-kafka" % sparkVersion) exclude ("org.spark-project.spark", "unused")

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

assemblyJarName in assembly := name.value + ".jar"