package kafka.consumer

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.*

class KafkaConsumer {

    companion object {

        fun createConsumer(host: String, port: String): KafkaConsumer<String, String> {
            val properties = Properties()
            properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$host:$port"
            properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
            properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
            properties[ConsumerConfig.GROUP_ID_CONFIG] = "CONSUMER-GROUP"
            properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
            properties[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = "1"

            return KafkaConsumer(properties)
        }

    }

}