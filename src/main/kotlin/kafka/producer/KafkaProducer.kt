package kafka.producer

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

class KafkaProducer {

    companion object {

        private val producers = mutableMapOf<String, KafkaProducer<String, String>>()

        fun createKafkaProducer(topic: String, host: String, port: String) {
            val properties = Properties()
            properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$host:$port"
            properties[ProducerConfig.CLIENT_ID_CONFIG] = "TEST_PRODUCER_$topic"
            properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
            properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
            producers[topic] = KafkaProducer(properties)
        }

        fun sendNumbers(topic: String, from: Long, to: Long) {
            val producer = producers[topic] ?: throw getNotFoundProducerException(topic)
            (from..to).forEach {
                postMessageToKafka(topic, it.toString(), producer)
            }
        }

        fun getNotFoundProducerException(topic: String) =
            IllegalArgumentException("Does not have any producer for this topic: $topic, please initialize them before send any message")

        private fun postMessageToKafka(topic: String, message: String, producer: KafkaProducer<String, String>) {
            val record = ProducerRecord<String, String>(topic, message)
            val metdata = producer.send(record).get()
            println("Record sent: $message to partition ${metdata.partition()} with offset ${metdata.offset()} ")
        }
    }

}
