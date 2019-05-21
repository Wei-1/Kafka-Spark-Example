// Wei Chen
// 2016-11-06
import java.util.HashMap

import org.apache.log4j.Logger
import org.apache.log4j.Level
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.{HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

class KafkaSink(createProducer: () => KafkaProducer[String, String]) extends Serializable {
    lazy val producer = createProducer()
    def send(topic: String, value: String): Unit = producer.send(new ProducerRecord(topic, value))
}
object KafkaSink {
    def apply(config: HashMap[String, Object]): KafkaSink = {
        val f = () => {
            val producer = new KafkaProducer[String, String](config)
            sys.addShutdownHook {
                producer.close()
            }
            producer
        }
        new KafkaSink(f)
    }
}

object KafkaConnector {

    def main(args: Array[String]): Unit = {
        if (args.length < 1) { // 2016-10-14
            System.err.println(s"""
                |Usage: KafkaConnector <kafka> <batchinterval>
                |  <kafka> is the list of one or more Kafka links. Separator ","
                |    EX: 127.0.0.1:9092
                |  <batchinterval> batch interval of minisecond - optional
                |    EX: 1000
                |  Example: spark-submit --master local[2] --class KafkaConnector engine.jar \
                |    127.0.0.1:9092 1000
                """.stripMargin)
            System.exit(1)
        }
        val kafkalinks = args(0)
        val batchinterval = ( try { args(1).toInt } catch { case e: Exception => 1000 } )

        Logger.getLogger("org").setLevel(Level.OFF)
        Logger.getLogger("akka").setLevel(Level.OFF)

        // Create context with 1 second batch interval
        val sparkConf = new SparkConf().setAppName("KafkaConnectorTest")
        val ssc = new StreamingContext(sparkConf, Milliseconds(batchinterval))

        val props = new HashMap[String, Object]()
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkalinks)
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringSerializer")
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringSerializer")
        val kafkaSink = ssc.sparkContext.broadcast(KafkaSink(props))
        kafkaSink.value.send("test", "0")

        // Create direct kafka stream with kafkalinks and topics
        val kafkaParams = Map[String, String]("bootstrap.servers" -> kafkalinks)

        val topicsSet = Array("test").toSet
        val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
            ssc, kafkaParams, topicsSet)
        messages.map { l =>
            val value = (try {
                val testparse = (l._2.toInt + 1).toString
                kafkaSink.value.send("test", testparse)
                testparse
            } catch { case e: Exception => l._2 })
            value
        }.print()

        // Start the computation
        ssc.start()
        ssc.awaitTermination()
    }
}
