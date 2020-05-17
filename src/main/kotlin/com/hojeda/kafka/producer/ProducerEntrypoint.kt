package com.hojeda.kafka.producer


fun main() {

    val topic = "numbers-topic"
    val host = "localhost"
    val port = "9092"

    KafkaProducer.createKafkaProducer(topic, host, port)

    KafkaProducer.sendNumbers(topic, 1, 100)
}