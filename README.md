# Kafka Spark Connector

This project shows the way to get topics directly from Kafka and send topics to kafka.

    Scala version 2.11.8
    Spark version 2.1.0

## Notes

The method this project used is Direct connect.

This method connect to Kafka without zookeeper, so there is no group.id set here.
If you want to use group.id, please use normal connection, which required at least 2 local cores.

The Spark version is 2.1.0

If one want to change to 1.6, you can change the spark version in build.sbt from 2.1.0 to 1.6.0 and delete the "-0-8" at the end of the spark-streaming-kafka package.

## Requirement

This program requires Kafka with "test" topic created.

## Build

    sbt assembly

## Usage

    KafkaConnector <kafka> <batchinterval>
    <kafka> is the list of one or more Kafka links. Separator ","
        EX: 127.0.0.1:9092
    <batchinterval> batch interval of minisecond - optional
        EX: 1000
    Example:
        spark-submit --master local[2] --class KafkaConnector engine.jar 127.0.0.1:9092 1000
