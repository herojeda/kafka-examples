package com.hojeda.kafka.producer

import com.hojeda.util.JsonFileLoader
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

class KafkaProducer {

    companion object {

        private val producers = mutableMapOf<String, KafkaProducer<String, String>>()

        fun createKafkaProducer(topic: String, host: String, port: String) {
            val properties = Properties()
            properties[BOOTSTRAP_SERVERS_CONFIG] = "$host:$port"
            properties[CLIENT_ID_CONFIG] = "TEST_PRODUCER_$topic"
            properties[KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
            properties[VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
            producers[topic] = KafkaProducer(properties)
        }

        fun sendNumbers(topic: String, from: Long, to: Long) {
            val producer = producers[topic] ?: throw getNotFoundProducerException(topic)
            (from..to).forEach {
                postMessageToKafka(topic, it.toString(), producer)
            }
        }

        fun sendMessages(topic: String, fileLocation: String, from: Long, to: Long) {
            val producer = producers[topic] ?: throw getNotFoundProducerException(topic)
            val json = JsonFileLoader.readFromFileJsonObject(fileLocation)
            (from..to).forEach {
                postMessageToKafka(topic, json.encode(), producer)
            }
        }

        fun sendMessage(topic: String, message: String) {
            val producer = producers[topic] ?: throw getNotFoundProducerException(topic)
            postMessageToKafka(topic, message, producer)
        }

        fun getNotFoundProducerException(topic: String) =
            IllegalArgumentException("Does not have any producer for this topic: $topic, please initialize them before send any message")

        private fun postMessageToKafka(topic: String, message: String, producer: KafkaProducer<String, String>) {
            val record = ProducerRecord(topic, 0, message, message)
            val metdata = producer.send(record).get()

            println("Record sent: $message to partition ${metdata.partition()} with offset ${metdata.offset()} ")
        }
    }

}
