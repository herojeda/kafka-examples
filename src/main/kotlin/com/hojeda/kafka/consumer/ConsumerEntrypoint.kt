package com.hojeda.kafka.consumer

fun main() {

    val topic = "numbers-topic"
    val host = "localhost"
    val port = "9092"

    val consumer = KafkaConsumer.createConsumer(host, port)

    consumer.subscribe(listOf(topic))

    while (true) {
        val records = consumer.poll(1000)
        println("******** -> Records is empty: ${records.isEmpty}")
        if (records.isEmpty) break
        println("******** -> Records: ${records.count()}")

        records.forEach { record ->
            println("Value: ${record.value()}")
        }
    }

    consumer.commitAsync()
}