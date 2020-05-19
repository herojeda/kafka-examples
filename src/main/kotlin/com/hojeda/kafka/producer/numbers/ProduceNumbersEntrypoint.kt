package com.hojeda.kafka.producer.numbers

import com.hojeda.kafka.producer.KafkaProducer


fun main() {

    val topic = "numbers-topic"
    val host = "localhost"
    val port = "9092"

    KafkaProducer.createKafkaProducer(topic, host, port)

    KafkaProducer.sendNumbers(topic, 1, 10000)
}